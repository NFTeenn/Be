package nfteen.dondon.dondon.home.service;

import nfteen.dondon.dondon.home.dto.*;
import nfteen.dondon.dondon.home.entity.Home;
import nfteen.dondon.dondon.home.entity.Quiz;
import nfteen.dondon.dondon.home.entity.Word;
import nfteen.dondon.dondon.home.repository.HomeRepository;
import nfteen.dondon.dondon.home.repository.QuizRepository;
import nfteen.dondon.dondon.home.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private WordRepository wordRepository;

    public HomeResponse processHome(HomeRequest request) throws Exception {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return null;
        }

        Home home = homeRepository.findById(request.getEmail()).orElse(null);

        List<String> mission;
        int day, level, quizCount;

        if (home != null) {
            if (home.getMission() == null || home.getMission().isEmpty()) {
                mission = new ArrayList<>();
                mission.add("1");
                for (int i = 1; i < 4; i++) mission.add("0");
            } else {
                mission = new ArrayList<>(List.of(home.getMission().replace("[","")
                        .replace("]","").replace("\"","").split(",")));
                while (mission.size() < 4) mission.add("0");
                mission.set(0, "1");
                for (int i = 1; i < mission.size(); i++) {
                    if (!"1".equals(mission.get(i))) mission.set(i, "0");
                }
            }

            LocalDate createDate = home.getCreateDate();
            LocalDate today = LocalDate.now();
            int calculatedDay = (int) ChronoUnit.DAYS.between(createDate, today) + 1;
            if (calculatedDay > home.getDay()) {
                home.setDay(calculatedDay);
            }

            home.setMission(mission.toString());
            homeRepository.save(home);

            day = home.getDay();
            level = home.getLevel();
            quizCount = home.getQuizCount();

        } else {
            mission = new ArrayList<>();
            mission.add("1");
            for (int i = 1; i < 4; i++) mission.add("0");

            Home newHome = Home.builder()
                    .email(request.getEmail())
                    .mission(mission.toString())
                    .day(1)
                    .level(0)
                    .quizCount(0)
                    .createDate(LocalDate.now())
                    .build();
            homeRepository.save(newHome);

            day = 1;
            level = 0;
            quizCount = 0;
        }

        String quiz = null;
        List<String> a = null;
        if (quizCount < 5) {
            List<Quiz> quizzes = quizRepository.findAll();
            if (!quizzes.isEmpty()) {
                Random rand = new Random();
                Quiz selected = quizzes.get(rand.nextInt(quizzes.size()));

                quiz = selected.getQuiz();

                if ("객관식퀴즈".equals(selected.getType())) {
                    a = new ArrayList<>();
                    a.add(selected.getA1());
                    a.add(selected.getA2());
                    a.add(selected.getA3());
                    a.add(selected.getA4());
                } else if ("OX퀴즈".equals(selected.getType())) {
                    a = null;
                }
            }
        }

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        List<String> words = new ArrayList<>();
        for (int i = 0; i < Math.min(6, allWords.size()); i++) {
            words.add(allWords.get(i).getWord());
        }

        return new HomeResponse(day, level, mission, quizCount, quiz, a, words);
    }

    public ShowWordResponse showWords(Object request) {
        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        List<String> words = new ArrayList<>();
        for (int i = 0; i < Math.min(4, allWords.size()); i++) {
            words.add(allWords.get(i).getWord());
        }
        return new ShowWordResponse(words);
    }

    public List<SearchWordResponse> searchWords(SearchWordRequest request) {
        String search = request.getSearch();
        if (search == null || search.isEmpty()) {
            return new ArrayList<>();
        }

        Home home = homeRepository.findById(request.getEmail()).orElse(null);
        if (home != null) {
            List<String> mission = new ArrayList<>(List.of(home.getMission()
                    .replace("[", "").replace("]", "").replace("\"", "").split(",")));

            while (mission.size() < 4) mission.add("0");
            mission.set(1, "1");
            home.setMission(mission.toString());
            homeRepository.save(home);
        }
        List<Word> words = wordRepository.findByWordContainingIgnoreCase(search);
        return words.stream()
                .map(word -> new SearchWordResponse(word.getWord(), word.getDescription(), word.getSubject()))
                .toList();
    }

}