import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PLATAFORMA DE LETRAMENTO DIGITAL - VERSÃO INTEGRAL
 * Foco: Jovens Aprendizes (14-24 anos)
 */

// --- 1. ENUMS E CONSTANTES ---
enum Selo { BRONZE, PRATA, OURO, EMBAIXADOR }

enum Categoria { BASICO, OFFICE, IA_FUTURO }

// --- 2. EXCEÇÕES ---
class PlataformaException extends RuntimeException {
    public PlataformaException(String msg) { super(msg); }
}

// --- 3. MODELOS (ENTIDADES) ---

class Aula {
    private Long id;
    private String titulo;
    private int xpRecompensa;

    public Aula(Long id, String titulo, int xp) {
        this.id = id;
        this.titulo = titulo;
        this.xpRecompensa = xp;
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getXpRecompensa() { return xpRecompensa; }
}

class Modulo {
    private String nome;
    private List<Aula> aulas = new ArrayList<>();

    public Modulo(String nome) { this.nome = nome; }
    public void addAula(Aula a) { aulas.add(a); }
    public List<Aula> getAulas() { return aulas; }
}

class Trilha {
    private String nome;
    private Categoria categoria;
    private Selo seloAlvo;
    private List<Modulo> modulos = new ArrayList<>();

    public Trilha(String nome, Categoria cat, Selo selo) {
        this.nome = nome;
        this.categoria = cat;
        this.seloAlvo = selo;
    }
    public void addModulo(Modulo m) { modulos.add(m); }
    public String getNome() { return nome; }
    public Selo getSeloAlvo() { return seloAlvo; }
    public List<Modulo> getModulos() { return modulos; }
}

class Usuario {
    private String nome;
    private int xp = 0;
    private Set<Long> aulasConcluidas = new HashSet<>();
    private List<String> conquistas = new ArrayList<>();

    public Usuario(String nome) { this.nome = nome; }
    
    public void ganharXp(int pts) { this.xp += pts; }
    public void registrarAula(Long id) { aulasConcluidas.add(id); }
    
    // Getters
    public String getNome() { return nome; }
    public int getXp() { return xp; }
    public Set<Long> getAulasConcluidas() { return aulasConcluidas; }
    public List<String> getConquistas() { return conquistas; }
}

class TopicoForum {
    private String titulo;
    private String autor;
    private List<String> respostas = new ArrayList<>();

    public TopicoForum(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
    }
    public void responder(String msg) { respostas.add(msg); }
}

// --- 4. SERVIÇOS (REGRAS DE NEGÓCIO) ---

class EducacionalService {
    private List<Usuario> rankingAlunos = new ArrayList<>();

    public void completarAula(Usuario aluno, Aula aula) {
        if (aluno.getAulasConcluidas().contains(aula.getId())) {
            throw new PlataformaException("Você já assistiu esta aula!");
        }
        aluno.registrarAula(aula.getId());
        aluno.ganharXp(aula.getXpRecompensa());
        System.out.println("[Progresso] " + aluno.getNome() + " concluiu: " + aula.getTitulo());
    }

    public void emitirCertificado(Usuario aluno, Trilha trilha) {
        // Verifica se todas as aulas de todos os módulos foram concluídas
        List<Long> todasAulasTrilha = trilha.getModulos().stream()
                .flatMap(m -> m.getAulas().stream())
                .map(Aula::getId)
                .collect(Collectors.toList());

        if (aluno.getAulasConcluidas().containsAll(todasAulasTrilha)) {
            String certificado = "CERTIFICADO " + trilha.getSeloAlvo() + " - " + trilha.getNome();
            aluno.getConquistas().add(certificado);
            System.out.println("🎓 Parabéns! Selo " + trilha.getSeloAlvo() + " desbloqueado para " + aluno.getNome());
        } else {
            System.out.println("❌ " + aluno.getNome() + ", ainda faltam aulas para concluir a trilha " + trilha.getNome());
        }
    }

    public void exibirRanking(List<Usuario> alunos) {
        System.out.println("\n--- 🏆 RANKING DE ENGAJAMENTO ---");
        alunos.stream()
            .sorted((u1, u2) -> Integer.compare(u2.getXp(), u1.getXp()))
            .forEach(u -> System.out.println(u.getNome() + " - " + u.getXp() + " XP"));
    }
}

// --- 5. PONTO DE ENTRADA (MAIN) ---

public class PlataformaApplication {
    public static void main(String[] args) {
        EducacionalService service = new EducacionalService();

        // Configuração da Trilha de IA (Futuro)
        Trilha trilhaIA = new Trilha("IA & Futuro", Categoria.IA_FUTURO, Selo.OURO);
        Modulo m1 = new Modulo("Fundamentos de IA");
        Aula a1 = new Aula(1L, "O que é ChatGPT?", 500);
        Aula a2 = new Aula(2L, "Prompt Engineering", 500);
        m1.addAula(a1); m1.addAula(a2);
        trilhaIA.addModulo(m1);

        // Alunos
        Usuario jovem1 = new Usuario("Gabriel");
        Usuario jovem2 = new Usuario("Beatriz");

        // Simulação de uso
        System.out.println("--- Iniciando Letramento Digital ---\n");
        
        service.completarAula(jovem1, a1);
        service.completarAula(jovem1, a2); // Gabriel termina a trilha
        
        service.completarAula(jovem2, a1); // Beatriz faz só metade

        // Emissão de Certificados
        service.emitirCertificado(jovem1, trilhaIA);
        service.emitirCertificado(jovem2, trilhaIA);

        // Fórum
        TopicoForum duvidaIA = new TopicoForum("Dúvida sobre Prompts", "Beatriz");
        duvidaIA.responder("Use o método Persona para melhores resultados!");

        // Ranking Final
        service.exibirRanking(Arrays.asList(jovem1, jovem2));
    }
}
