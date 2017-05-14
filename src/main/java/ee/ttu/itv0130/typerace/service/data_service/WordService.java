package ee.ttu.itv0130.typerace.service.data_service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ee.ttu.itv0130.typerace.service.model.Word;
import ee.ttu.itv0130.typerace.service.repository.WordRepository;

@Service
public class WordService {
	@Autowired
	private WordRepository wordRepository;

	public String getRandomWord() {
		Integer count = Long.valueOf(wordRepository.count()).intValue();
		String chars = null;
		if (count > 0) {
			while (chars == null) {
				Integer randomIndex = ThreadLocalRandom.current().nextInt(0, count + 1);
				Page<Word> wordPage = wordRepository.findAll(new PageRequest(randomIndex, 1));
				
				if (wordPage.hasContent()) {
					chars = wordPage.getContent().get(0).getChars();
				}
			}
		}
		
		return chars;
	}
}
