package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.projetoTCC.arCondicionado.arCondicionado.config.JwtService;
import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginRequest;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginResponse;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.UsuarioDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }
    public Usuario salvarUsuario(Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        Usuario salvo = usuarioRepository.save(usuario);
        return salvo;
    }

    public byte[] salvarUsuarioExcel(MultipartFile file) {
        // 1. Valida se o arquivo está vazio
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio.");
        }

        try (
                // 2. Abre o arquivo original para leitura
                InputStream inputStream = file.getInputStream();
                Workbook originalWorkbook = new XSSFWorkbook(inputStream);

                // 3. Cria um novo workbook na memória para a resposta
                Workbook newWorkbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet originalSheet = originalWorkbook.getSheetAt(0);
            Sheet newSheet = newWorkbook.createSheet("Status de Usuários");

            List<Usuario> usuariosParaSalvar = new ArrayList<>();

            // 4. Cria a linha do cabeçalho na nova planilha
            Row newHeaderRow = newSheet.createRow(0);
            Row originalHeaderRow = originalSheet.getRow(0);

            if (originalHeaderRow != null) {
                // Copia os cabeçalhos originais
                for (int i = 0; i < originalHeaderRow.getLastCellNum(); i++) {
                    Cell originalCell = originalHeaderRow.getCell(i);
                    Cell newCell = newHeaderRow.createCell(i);
                    if (originalCell != null) {
                        newCell.setCellValue(originalCell.getStringCellValue());
                    }
                }
            }

            // Adiciona a nova coluna "Status" ao cabeçalho
            Cell statusHeaderCell = newHeaderRow.createCell(newHeaderRow.getLastCellNum());
            statusHeaderCell.setCellValue("Status");

            // 5. Itera sobre as linhas do arquivo original
            for (int i = 1; i <= originalSheet.getLastRowNum(); i++) {
                Row originalRow = originalSheet.getRow(i);
                if (originalRow == null) continue;

                // 6. Lê os dados da linha
                String nome = originalRow.getCell(0).getStringCellValue();
                long matricula = (long) originalRow.getCell(1).getNumericCellValue();
                String senha = originalRow.getCell(2).getStringCellValue();
                String tipoStr = originalRow.getCell(3).getStringCellValue();

                // 7. Verifica se o usuário já existe no banco de dados
                Optional<Usuario> usuarioExistente = usuarioRepository.findByMatricula(matricula);

                // 8. Cria a nova linha na planilha de resposta
                Row newRow = newSheet.createRow(i);

                // 9. Copia os dados originais para a nova linha
                newRow.createCell(0).setCellValue(nome);
                newRow.createCell(1).setCellValue(matricula);
                newRow.createCell(2).setCellValue(senha);
                newRow.createCell(3).setCellValue(tipoStr);

                // 10. Preenche a nova coluna "Status" e decide se deve salvar
                Cell statusCell = newRow.createCell(newRow.getLastCellNum());
                if (usuarioExistente.isPresent()) {
                    statusCell.setCellValue("Já existe");
                } else {
                    statusCell.setCellValue("Novo");
                    // Prepara o usuário para ser salvo
                    TipoUsuarioEnum tipo = TipoUsuarioEnum.valueOf(tipoStr.toUpperCase());
                    Usuario novoUsuario = Usuario.builder()
                            .nome(nome)
                            .matricula(matricula)
                            .senha(encoder.encode(senha))
                            .tipo(tipo)
                            .build();
                    usuariosParaSalvar.add(novoUsuario);
                }
            }

            // 11. Salva todos os novos usuários no banco de dados
            if (!usuariosParaSalvar.isEmpty()) {
                usuarioRepository.saveAll(usuariosParaSalvar);
            }

            // 12. Escreve a nova planilha no outputStream
            newWorkbook.write(outputStream);

            // 13. Retorna o array de bytes da planilha
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro de validação de dados no arquivo: " + e.getMessage(), e);
        }
    }

    public LoginResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(String.valueOf(request.getMatricula()), request.getSenha()));

        Usuario usuario = usuarioRepository.findByMatricula(request.getMatricula())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String roleName = "ROLE_" + usuario.getTipo().name();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        String token = jwtService.generateToken(new User(
                String.valueOf(usuario.getMatricula()),
                "", authorities
        ));
        LoginResponse loginResponse = new LoginResponse(usuario.getId(), usuario.getNome(),
                usuario.getMatricula().toString(),token, usuario.getTipo());
        return loginResponse;
    }

    public List<UsuarioDTO> professores() {
        List<Usuario> professores = usuarioRepository.buscarProfessores();
        List<UsuarioDTO> usuarioDTO = professores.stream().map(s ->
                new UsuarioDTO(s.getMatricula(),s.getNome())
        ).collect(Collectors.toList());
        return usuarioDTO;
    }
}
