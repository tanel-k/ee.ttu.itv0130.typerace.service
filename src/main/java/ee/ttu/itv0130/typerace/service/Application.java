package ee.ttu.itv0130.typerace.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
/*
	@Autowired
	private WordRepository wordRepository;
*/
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	/*
		File file = new ClassPathResource("words.txt").getFile();
		FileReader reader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {
				Word word = new Word();
				word.setChars(line);
				wordRepository.save(word);
			}
		}
		
		bufferedReader.close();
	*/
	}
}
