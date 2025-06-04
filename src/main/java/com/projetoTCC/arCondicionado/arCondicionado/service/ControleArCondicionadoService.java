package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.projetoTCC.arCondicionado.arCondicionado.enums.ModoArCondicionadoEnum;
import com.projetoTCC.arCondicionado.arCondicionado.enums.VelocidadeVentiladorEnum;
import com.projetoTCC.arCondicionado.arCondicionado.model.ConexaoESPDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroAparelhoDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ControleArCondicionadoUpdateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ControleArCondicionadoService {

    private final ControleArCondicionadoRepository repository;

    public ControleArCondicionadoService(ControleArCondicionadoRepository repository) {
        this.repository = repository;
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

        return repository.save(novo);
    }
    public String gerarCodigoArduino(Long id) {
        StringBuilder sb = new StringBuilder();

        sb.append("#include <ESP8266WiFi.h>\n")
                .append("#include <ESP8266HTTPClient.h>\n")
                .append("#include <ArduinoJson.h>\n\n")

                .append("const String servidorBase = \"http://SEU_SERVIDOR\";\n")
                .append("const int dispositivoId = ").append(id).append(";\n\n")

                .append("String ssid = \"\";\n")
                .append("String password = \"\";\n\n")

                .append("void setup() {\n")
                .append("  Serial.begin(115200);\n\n")

                .append("  // Passo 1: Buscar credenciais Wi-Fi\n")
                .append("  HTTPClient http;\n")
                .append("  String urlConexao = servidorBase + \"/api/esp/conexao/\" + String(dispositivoId);\n")
                .append("  http.begin(urlConexao);\n")
                .append("  int httpCode = http.GET();\n")
                .append("  if (httpCode == 200) {\n")
                .append("    String payload = http.getString();\n")
                .append("    StaticJsonDocument<256> doc;\n")
                .append("    DeserializationError error = deserializeJson(doc, payload);\n")
                .append("    if (!error) {\n")
                .append("      ssid = doc[\"ssid\"].as<String>();\n")
                .append("      password = doc[\"senha\"].as<String>();\n")
                .append("      Serial.println(\"Credenciais WiFi recebidas.\");\n")
                .append("    } else {\n")
                .append("      Serial.println(\"Erro ao parsear JSON de credenciais.\");\n")
                .append("      return;\n")
                .append("    }\n")
                .append("  } else {\n")
                .append("    Serial.println(\"Erro ao obter credenciais WiFi. Código HTTP: \" + String(httpCode));\n")
                .append("    return;\n")
                .append("  }\n")
                .append("  http.end();\n\n")

                .append("  // Conectar ao Wi-Fi\n")
                .append("  WiFi.begin(ssid.c_str(), password.c_str());\n")
                .append("  Serial.print(\"Conectando ao WiFi\");\n")
                .append("  while (WiFi.status() != WL_CONNECTED) {\n")
                .append("    delay(1000);\n")
                .append("    Serial.print(\".\");\n")
                .append("  }\n")
                .append("  Serial.println(\"\\nConectado ao WiFi!\");\n")
                .append("}\n\n")

                .append("void loop() {\n")
                .append("  if (WiFi.status() == WL_CONNECTED) {\n")
                .append("    HTTPClient http;\n")
                .append("    String urlComando = servidorBase + \"/api/esp/comando/\" + String(dispositivoId);\n")
                .append("    http.begin(urlComando);\n")
                .append("    int httpCode = http.GET();\n")
                .append("    if (httpCode == 200) {\n")
                .append("      String payload = http.getString();\n")
                .append("      Serial.println(\"Comando recebido: \" + payload);\n")
                .append("      StaticJsonDocument<200> doc;\n")
                .append("      DeserializationError error = deserializeJson(doc, payload);\n")
                .append("      if (!error) {\n")
                .append("        String acao = doc[\"acao\"];\n")
                .append("        int temperatura = doc[\"temperatura\"];\n")
                .append("        if (acao == \"LIGAR\") {\n")
                .append("          Serial.println(\"LIGANDO AR\");\n")
                .append("        } else if (acao == \"DESLIGAR\") {\n")
                .append("          Serial.println(\"DESLIGANDO AR\");\n")
                .append("        } else if (acao == \"MUDAR_TEMPERATURA\") {\n")
                .append("          Serial.println(\"TEMPERATURA PARA \" + String(temperatura));\n")
                .append("        }\n")
                .append("      } else {\n")
                .append("        Serial.println(\"Erro ao parsear comando JSON\");\n")
                .append("      }\n")
                .append("    } else {\n")
                .append("      Serial.println(\"Erro ao consultar comando. Código HTTP: \" + String(httpCode));\n")
                .append("    }\n")
                .append("    http.end();\n")
                .append("  } else {\n")
                .append("    Serial.println(\"WiFi desconectado, tentando reconectar...\");\n")
                .append("    WiFi.begin(ssid.c_str(), password.c_str());\n")
                .append("  }\n")
                .append("  delay(5000);\n")
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
        return ResponseEntity.ok(resposta);
    }
    public ResponseEntity<?> atualizarControle(Long id, ControleArCondicionadoUpdateDTO dto) {
        Optional<ControleArCondicionado> controleOpt = repository.findById(id);

        if (controleOpt.isEmpty()) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("mensagem", "Dispositivo não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        }

        ControleArCondicionado controle = controleOpt.get();

        // Atualiza somente se o campo vier não nulo (para permitir updates parciais)
        if (dto.getLigado() != null) controle.setLigado(dto.getLigado());
        if (dto.getTemperatura() != null) controle.setTemperatura(dto.getTemperatura());
        if (dto.getModo() != null) controle.setModo(dto.getModo());
        if (dto.getVelocidade() != null) controle.setVelocidade(dto.getVelocidade());
        if (dto.getSwingAtivo() != null) controle.setSwingAtivo(dto.getSwingAtivo());

        controle.setUltimaAtualizacao(LocalDateTime.now());

        repository.save(controle);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("mensagem", "Dispositivo atualizado com sucesso");
        resposta.put("controle", controle);

        return ResponseEntity.ok(resposta);
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
        }).collect(Collectors.toList()); // CORRETO
    }
}
