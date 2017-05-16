package ee.ttu.itv0130.typerace.service.rest.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ee.ttu.itv0130.typerace.service.data_service.ScoreService;
import ee.ttu.itv0130.typerace.service.data_service.objects.PlayerScores;

@RestController
public class ScoreController {
	@Autowired
	private ScoreService scoreService;

	@RequestMapping(value = "/scores/{sessionId}", method = RequestMethod.GET)
	public PlayerScores getSessionScores(@PathVariable String sessionId, @RequestParam(name="afterIndex", required=false) Integer afterIndex) {
		return scoreService.getWithoutCreate(sessionId, Optional.ofNullable(afterIndex));
	}
}
