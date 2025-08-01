package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoTCC.arCondicionado.arCondicionado.config.EspWebSocketHandler;
import com.projetoTCC.arCondicionado.arCondicionado.enums.ModoArCondicionadoEnum;
import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import com.projetoTCC.arCondicionado.arCondicionado.enums.VelocidadeVentiladorEnum;
import com.projetoTCC.arCondicionado.arCondicionado.model.ConexaoESPDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroAparelhoDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ControleArCondicionadoUpdateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.projetoTCC.arCondicionado.arCondicionado.model.enums.MarcaAC.GOODWEATHER;
import static com.projetoTCC.arCondicionado.arCondicionado.service.utils.UsuarioUtils.getUsuarioLogado;

@Service

public class ControleArCondicionadoService {
    private final ControleArCondicionadoRepository repository;
    private final Zhjt03Service zhjt03Service;
    private final ReservaSalaRepository reservaRepository;
    private final SalaService salaService;
    private final EspWebSocketHandler webSocketHandler;

    public ControleArCondicionadoService(ControleArCondicionadoRepository repository, Zhjt03Service zhjt03Service, ReservaSalaRepository reservaRepository, SalaService salaService, EspWebSocketHandler webSocketHandler) {
        this.repository = repository;
        this.zhjt03Service = zhjt03Service;
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
        this.webSocketHandler = webSocketHandler;
    }

    @Transactional(readOnly = true)
    public ConexaoESPDTO buscarDadosConexaoPorId(Long id) {
        ControleArCondicionado ar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispositivo não encontrado com ID: " + id));

        if (ar.getSsid() == null || ar.getSenhaWifi() == null) {
            throw new RuntimeException("Credenciais de conexão não encontradas para o ID: " + id);
        }

        return new ConexaoESPDTO(ar.getSsid(), ar.getSenhaWifi());
    }
    @Transactional
    public ControleArCondicionado cadastrarAparelho(CadastroAparelhoDTO dto) {
        Usuario usuario = getUsuarioLogado();
        boolean ehAdmin = usuario.getTipo() == TipoUsuarioEnum.ADMINISTRATIVO;
        if(!ehAdmin){
            throw new RuntimeException("Você não possui permissao para cadastrar um novo ar");
        }
        ControleArCondicionado novo = new ControleArCondicionado();
        novo.setMarca(dto.getMarca());
        novo.setNome(dto.getNome());
        novo.setSsid(dto.getSsid());
        novo.setSenhaWifi(dto.getSenhaWifi());

        novo.setLigado(false);
        novo.setTemperatura(20);
        novo.setModo(ModoArCondicionadoEnum.FRIO);
        novo.setVelocidade(VelocidadeVentiladorEnum.BAIXA);
        novo.setSwingAtivo(false);
        novo.setSala(salaService.salaByid(dto.getSalaId()));

        return repository.save(novo);
    }
    public String gerarCodigoArduino(Long id) {
        ControleArCondicionado controleOpt = repository.findById(id).orElseThrow(() -> new RuntimeException("Dispositivo não encontrado") );
        StringBuilder sb = new StringBuilder();

        sb.append("#include <WiFi.h>\n")
                .append("#include <HTTPClient.h>\n")
                .append("#include <ArduinoJson.h>\n")
                .append("#include <IRremoteESP8266.h>\n")
                .append("#include <IRsend.h>\n")
                .append("#include <IRutils.h>\n\n")

                .append("// Bibliotecas específicas por marca\n")
                .append("#include <ir_LG.h>\n")
                .append("#include <ir_Samsung.h>\n")
                .append("#include <ir_Midea.h>\n")
                .append("#include <ir_Daikin.h>\n")
                .append("#include <ir_Panasonic.h>\n")
                .append("#include <ir_Fujitsu.h>\n")
                .append("#include <ir_Gree.h>\n")
                .append("#include <ir_Toshiba.h>\n")
                .append("#include <ir_Hitachi.h>\n")
                .append("#include <ir_Coolix.h>\n\n")

                // Credenciais e servidor base (atualmente hardcoded, mas podem ser dinâmicos no futuro)
                .append("const char* ssid = ").append(controleOpt.getSsid()).append(";\n")
                .append("const char* password = ").append(controleOpt.getSenhaWifi()).append(";\n")
                .append("const char* servidorBase = \"http://10.5.250.12:8080\";\n")
                .append("const int dispositivoId = ").append(id).append(";\n\n") // Usando o ID passado como parâmetro

                .append("const uint16_t kIrLed = 4;  // GPIO do IR LED\n")
                .append("IRsend irsend(kIrLed);\n\n")

                .append("String ultimaAcao = \"\";\n")
                .append("int ultimaTemp = 20;\n\n")

                .append("void setup() {\n")
                .append("  Serial.begin(115200);\n")
                .append("  irsend.begin();\n\n")

                .append("  WiFi.begin(ssid, password);\n")
                .append("  Serial.print(\"Conectando ao WiFi\");\n")
                .append("  while (WiFi.status() != WL_CONNECTED) {\n")
                .append("    delay(500);\n")
                .append("    Serial.print(\".\");\n")
                .append("  }\n")
                .append("  Serial.println(\"\\nConectado! IP: \" + WiFi.localIP().toString());\n")
                .append("}\n\n")

                .append("void loop() {\n")
                .append("  // Se estiver conectado, tenta buscar comandos no servidor\n")
                .append("  if (WiFi.status() == WL_CONNECTED) {\n")
                .append("    HTTPClient http;\n")
                .append("    String url = String(servidorBase) + \"/api/esp/comandos/\" + String(dispositivoId);\n")
                .append("    http.begin(url);\n\n")

                .append("    int httpCode = http.GET();\n\n")

                .append("    if (httpCode == 200) {\n")
                .append("      String payload = http.getString();\n")
                .append("      Serial.println(\"Resposta do servidor: \" + payload);\n\n")

                .append("      StaticJsonDocument<512> doc;\n")
                .append("      DeserializationError error = deserializeJson(doc, payload);\n\n")

                .append("      if (!error) {\n")
                .append("        // Lê valores do JSON, com fallback\n")
                .append("        String acao = doc[\"acao\"] | \"\";\n")
                .append("        int temperatura = doc[\"temperatura\"] | 0;\n")
                .append("        const char* modo = doc[\"modo\"] | \"\";\n")
                .append("        const char* velocidade = doc[\"velocidade\"] | \"\";\n")
                .append("        bool swingAtivo = doc[\"swingAtivo\"] | false;\n")
                .append("        String marca = doc[\"marca\"] | \"LG\";\n")
                .append("        JsonArray codigoRaw = doc[\"codigoRaw\"];\n")
                .append("        // Converte para maiúsculas (modifica a string)\n")
                .append("        marca.toUpperCase();\n")
                .append("        acao.toUpperCase();\n\n")

                .append("        // Só envia se mudou a ação ou temperatura\n")
                .append("        //if (acao != ultimaAcao || temperatura != ultimaTemp) {\n")
                .append("          enviarComando(marca, acao, temperatura, modo, velocidade, swingAtivo, codigoRaw);\n")
                .append("          ultimaAcao = acao;\n")
                .append("          ultimaTemp = temperatura;\n")
                .append("        //}\n")
                .append("      } else {\n")
                .append("        Serial.println(\"Erro ao interpretar JSON\");\n")
                .append("      }\n")
                .append("    } else {\n")
                .append("      Serial.printf(\"Erro HTTP: %d\\n\", httpCode);\n")
                .append("    }\n\n")

                .append("    http.end(); // fecha a conexão HTTP\n\n")

                .append("  } else {\n")
                .append("    Serial.println(\"WiFi desconectado. Reconectando...\");\n")
                .append("    WiFi.begin(ssid, password);\n")
                .append("  }\n\n")

                .append("  delay(5000);\n")
                .append("}\n\n")

                .append("void enviarComando(String marca, String acao, int temperatura, const char* modo, const char* velocidade, bool swingAtivo, JsonArray codigoRaw) {\n")
                .append("  Serial.println(\"Enviando comando IR para: \" + marca);\n\n")

                .append("  if (marca == \"LG\") {\n")
                .append("    IRLgAc ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kLgAcCool);\n")
                .append("    ac.setFan(kLgAcFanHigh);\n")
                .append("    if (acao == \"LIGAR\") ac.on(); else ac.off();\n")
                .append("    ac.send();\n")
                .append("  }\n")
                .append("  \n")
                .append("  else if (marca == \"ZHJT03\") {\n")
                .append("  Serial.println(\"Marca ZHJT03 detectada, enviando código RAW...\");\n\n\n")

                .append("  irsend.sendNEC(0x6DEFF85F, 50);// 38kHz é a frequência típica do CHIGO/ZHJT03\n")
                .append("  Serial.println(\"Código IR RAW enviado.\");\n")
                .append("}\n")
                .append("if (marca == \"GOODWEATHER\") {\n")
                .append("  if (!codigoRaw.isNull() && codigoRaw.size() > 0) {\n")
                .append("    String codigoStr = codigoRaw[0];\n")
                .append("    Serial.println(codigoStr);\n")
                .append("    uint64_t codigo = strtoull(codigoStr.c_str(), NULL, 16);\n")
                .append("  irsend.sendGoodweather(codigo, 48);\n")
                .append("  }\n")
                .append("}\n\n")

                .append("  else if (marca == \"SAMSUNG\") {\n")
                .append("    IRSamsungAc ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kSamsungAcCool);\n")
                .append("    ac.setFan(kSamsungAcFanAuto);\n")
                .append("    ac.setSwing(swingAtivo);\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"MIDEA\") {\n")
                .append("    IRMideaAC ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kMideaACCool);\n")
                .append("    ac.setFan(kMideaACFanHigh);\n")
                .append("    // A biblioteca IRMideaAC não possui o método setSwingVertical\n")
                .append("    // Portanto, omitimos o controle de swing vertical\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"DAIKIN\") {\n")
                .append("    IRDaikinESP ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setFan(kDaikinFanAuto);\n")
                .append("    ac.setMode(kDaikinCool);\n")
                .append("    ac.setSwingVertical(swingAtivo ? kDaikinSwingOn : kDaikinSwingOff);\n")
                .append("    if (acao == \"LIGAR\") ac.on(); else ac.off();\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"PANASONIC\") {\n")
                .append("    IRPanasonicAc ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kPanasonicAcCool);\n")
                .append("    ac.setFan(kPanasonicAcFanAuto);\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"FUJITSU\") {\n")
                .append("    IRFujitsuAC ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kFujitsuAcModeCool);\n")
                .append("    // A biblioteca IRFujitsuAC não possui o método setFan\n")
                .append("    // Portanto, omitimos o controle de velocidade do ventilador\n")
                .append("    if (acao == \"LIGAR\") ac.setPower(true); else ac.setPower(false);\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"GREE\") {\n")
                .append("    IRGreeAC ac(kIrLed); \n")
                .append("    ac.begin();\n")
                .append("    ac.setFan(1);\n")
                .append("  ac.setMode(kGreeCool);\n")
                .append("  ac.setTemp(temperatura);  // 16-30C\n")
                .append("  ac.setSwingVertical(true, kGreeSwingAuto);\n")
                .append("  ac.setXFan(false);\n")
                .append("  ac.setLight(true);\n")
                .append("  ac.setSleep(false);\n")
                .append("  ac.setTurbo(false);\n")
                .append("    if(acao == \"LIGAR\"){\n")
                .append("      ac.on();\n")
                .append("    }else{\n")
                .append("      ac.off();\n")
                .append("    }\n")
                .append("    ac.send();\n")
                .append("  }\n\n\n")

                .append("else if (marca == \"KOMECO\" || marca == \"COOLIX\") {\n")
                .append("    Serial.println(\"Marca KOMECO/COOLIX detectada, usando protocolo COOLIX.\");\n")
                .append("    IRCoolixAC ac(kIrLed); \n")
                .append("    ac.begin();\n\n")

                .append("    // Configura o estado do ar condicionado\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.setTemp(temperatura);\n\n")

                .append("    // Lógica para o MODO (já estava correta)\n")
                .append("    if (strcmp(modo, \"COOL\") == 0) {\n")
                .append("        ac.setMode(kCoolixCool);\n")
                .append("    } else if (strcmp(modo, \"HEAT\") == 0) {\n")
                .append("        ac.setMode(kCoolixHeat);\n")
                .append("    } else if (strcmp(modo, \"DRY\") == 0) {\n")
                .append("        ac.setMode(kCoolixDry);\n")
                .append("    } else { \n")
                .append("        ac.setMode(kCoolixAuto);\n")
                .append("    }\n\n")

                .append("    // --- LÓGICA CORRIGIDA PARA VELOCIDADE do Ventilador ---\n")
                .append("    if (strcmp(velocidade, \"HIGH\") == 0) {\n")
                .append("        ac.setFan(kCoolixFanMax); // CORRIGIDO de kCoolixFanHigh para kCoolixFanMax\n")
                .append("    } else if (strcmp(velocidade, \"MEDIUM\") == 0) {\n")
                .append("        ac.setFan(kCoolixFanMed); // CORRIGIDO de kCoolixFanMedium para kCoolixFanMed\n")
                .append("    } else if (strcmp(velocidade, \"LOW\") == 0) {\n")
                .append("        ac.setFan(kCoolixFanMin); // CORRIGIDO de kCoolixFanLow para kCoolixFanMin\n")
                .append("    } else { // Padrão para Automático\n")
                .append("        ac.setFan(kCoolixFanAuto);\n")
                .append("    }\n")
                .append("    \n")
                .append("    // Envia o comando IR\n")
                .append("    ac.send();\n")
                .append("}\n\n\n")

                .append("  else if (marca == \"TOSHIBA\") {\n")
                .append("    IRToshibaAC ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kToshibaAcCool);\n")
                .append("    ac.setFan(kToshibaAcFanAuto);\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else if (marca == \"HITACHI\") {\n")
                .append("    IRHitachiAc ac(kIrLed); ac.begin();\n")
                .append("    ac.setTemp(temperatura);\n")
                .append("    ac.setMode(kHitachiAcCool);\n")
                .append("    ac.setFan(kHitachiAcFanAuto);\n")
                .append("    ac.setSwingVertical(swingAtivo);\n")
                .append("    ac.setPower(acao == \"LIGAR\");\n")
                .append("    ac.send();\n")
                .append("  }\n\n")

                .append("  else {\n")
                .append("    Serial.println(\"Marca não suportada: \" + marca);\n")
                .append("    return;\n")
                .append("  }\n\n")

                .append("  Serial.printf(\"Comando enviado: %s - %d°C - Modo: %s - Velocidade: %s - Swing: %s\\n\",\n")
                .append("                acao.c_str(), temperatura, modo, velocidade, swingAtivo ? \"ON\" : \"OFF\");\n")
                .append("}\n");

        return sb.toString();
    }


    public ResponseEntity<?> buscarComandoParaEsp(Long id) {
        Optional<ControleArCondicionado> controleOpt = repository.findById(id);

        if (controleOpt.isEmpty()) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Dispositivo não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        }

        ControleArCondicionado controle = controleOpt.get();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Comando carregado com sucesso");

        // Usamos o boolean ligado para retornar uma string de ação
        resposta.put("acao", controle.isLigado() ? "LIGAR" : "DESLIGAR");

        resposta.put("temperatura", controle.getTemperatura());
        resposta.put("modo", controle.getModo().name());
        resposta.put("velocidade", controle.getVelocidade().name());
        resposta.put("swingAtivo", controle.isSwingAtivo());
        resposta.put("marca", controle.getMarca());
        List<String> raw = controle
                .getMarca()== GOODWEATHER ? zhjt03Service.gerarCodigoIr(controle.getModo().name(), controle.getTemperatura(), controle.getVelocidade().name(), true, controle.isLigado()):
                List.of("");
        resposta.put("codigoRaw", raw);
        return ResponseEntity.ok(resposta);
    }

    @Transactional
    public ResponseEntity<?> atualizarControle(Long id, ControleArCondicionadoUpdateDTO dto) {
        ControleArCondicionado controleOpt = repository.findById(id).orElseThrow(() -> new RuntimeException("Dispositivo não encontrado") );

        Usuario usuario = getUsuarioLogado();

        Sala sala = controleOpt.getSala();
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek dia = agora.getDayOfWeek();
        LocalTime hora = agora.toLocalTime();

        ReservaSala reservaAtiva = reservaRepository.findAtiva(sala.getId(), dia, hora);

        boolean ehAdmin = usuario.getTipo() == TipoUsuarioEnum.ADMINISTRATIVO;
        boolean ehResponsavel = false;
        if(Objects.nonNull(reservaAtiva)){
            ehResponsavel = reservaAtiva.getUsuario().getId().equals(usuario.getId());
        }


        if (Objects.isNull(reservaAtiva) || ehAdmin || ehResponsavel) {

            // Atualiza somente se o campo vier não nulo (para permitir updates parciais)
        if (dto.getLigado() != null) controleOpt.setLigado(dto.getLigado());
        if (dto.getTemperatura() != null) controleOpt.setTemperatura(dto.getTemperatura());
        if (dto.getModo() != null) controleOpt.setModo(dto.getModo());
        if (dto.getVelocidade() != null) controleOpt.setVelocidade(dto.getVelocidade());
        if (dto.getSwingAtivo() != null) controleOpt.setSwingAtivo(dto.getSwingAtivo());

        controleOpt.setUltimaAtualizacao(LocalDateTime.now());

        repository.save(controleOpt);
        enviarComando(controleOpt);
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Dispositivo atualizado com sucesso");
        resposta.put("controle", controleOpt);

        return ResponseEntity.ok(resposta);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você não tem permissão para alterar esse ar-condicionado.");
        }

    }

    public void enviarComando(ControleArCondicionado controleOpt) {
        Map<String, Object> comando = new HashMap<>();
        comando.put("acao", controleOpt.isLigado() ? "LIGAR" : "DESLIGAR");
        comando.put("temperatura", controleOpt.getTemperatura());
        comando.put("modo", controleOpt.getModo().name());
        comando.put("velocidade", controleOpt.getVelocidade().name());
        comando.put("swingAtivo", controleOpt.isSwingAtivo());
        comando.put("marca", controleOpt.getMarca());

        if (controleOpt.getMarca() == GOODWEATHER) {
            List<String> raw = zhjt03Service.gerarCodigoIr(
                    controleOpt.getModo().name(),
                    controleOpt.getTemperatura(),
                    controleOpt.getVelocidade().name(),
                    true,
                    controleOpt.isLigado()
            );
            comando.put("codigoRaw", raw);
        } else {
            comando.put("codigoRaw", List.of(""));
        }

        try {
            String jsonComando = new ObjectMapper().writeValueAsString(comando);
            webSocketHandler.enviarComandoParaDispositivo(controleOpt.getId().toString(), jsonComando);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> buscarTodos() {
        List<ControleArCondicionado> controles = repository.findAll();

        return controles.stream().map(controle -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", controle.getId());
            item.put("mensagem", "Comando carregado com sucesso");
            item.put("acao", controle.isLigado() ? "LIGAR" : "DESLIGAR");
            item.put("temperatura", controle.getTemperatura());
            item.put("modo", controle.getModo().name());
            item.put("velocidade", controle.getVelocidade().name());
            item.put("swingAtivo", controle.isSwingAtivo());
            item.put("marca",controle.getMarca());
            return item;
        }).collect(Collectors.toList());
    }
    @Transactional
    public void deletarAr(Long id) {
        repository.deletarPorId(id);
    }
}
