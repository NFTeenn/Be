package nfteen.dondon.dondon.home.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.event.HomeLevelUpEvent;
import nfteen.dondon.dondon.home.dto.*;
import nfteen.dondon.dondon.home.entity.Home;
import nfteen.dondon.dondon.home.entity.Quiz;
import nfteen.dondon.dondon.home.entity.Word;
import nfteen.dondon.dondon.home.repository.HomeRepository;
import nfteen.dondon.dondon.home.repository.QuizRepository;
import nfteen.dondon.dondon.home.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Service
public class HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private WordRepository wordRepository;

    private final ApplicationEventPublisher publisher;

    public void levelUp(Home home) {
        home.setLevel(home.getLevel() + 1);

        publisher.publishEvent(
                new HomeLevelUpEvent(home.getEmail(), home.getLevel())
        );
    }

    private void updateDailyStatus(Home home) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate createDate = home.getCreateDate();
        int calculatedDay = (int)ChronoUnit.DAYS.between(createDate, today) + 1;

        if (calculatedDay > home.getDay()) {
            home.setDay(calculatedDay);
            List<String> mission = new ArrayList<>();
            for (int i = 0; i < 4; i++) mission.add("0");
            home.setMission(mission.toString());
            home.setQuizCount(0);

            homeRepository.save(home);
        }
    }

    public HomeResponse processHome(HomeRequest request) throws Exception {
        Home home = homeRepository.findById(request.getEmail()).orElse(null);
        List<String> mission = new ArrayList<>();
        int day, level, quizCount;

        if (home != null) {
            updateDailyStatus(home);
            for (String s : home.getMission()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")) {
                mission.add(s.trim());
            }
            if ("0".equals(mission.get(0))) {
                mission.set(0, "1");
                levelUp(home);
            }

            if (request.isSolve()) {
                home.setQuizCount(home.getQuizCount() + 1);
            }

            home.setMission(mission.toString());
            homeRepository.save(home);

            day = home.getDay();
            level = home.getLevel();
            quizCount = home.getQuizCount();

        } else {
            mission.add("1");
            for (int i = 1; i < 4; i++) mission.add("0");

            Home newHome = Home.builder()
                    .email(request.getEmail())
                    .mission(mission.toString())
                    .day(1)
                    .level(1)
                    .quizCount(0)
                    .createDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                    .build();
            homeRepository.save(newHome);

            day = 1;
            level = 0;
            quizCount = 0;
        }

        String quiz = null;
        List<String> a = null;
        int result = 0;
        String content = null;

        if (quizCount < 5) {
            List<Quiz> quizzes = quizRepository.findAll();
            if (!quizzes.isEmpty()) {
                Quiz selected = quizzes.get(new Random().nextInt(quizzes.size()));

                quiz = selected.getQuiz();
                content = selected.getContent();

                String rawResult = selected.getResult();
                if ("객관식퀴즈".equals(selected.getType())) {
                    a = List.of(
                            selected.getA1(),
                            selected.getA2(),
                            selected.getA3(),
                            selected.getA4()
                    );
                    if (rawResult != null && rawResult.contains("번")) {
                        result = Integer.parseInt(rawResult.replace("번", "").trim());
                    }
                } else if ("OX퀴즈".equals(selected.getType())) {
                    result = rawResult.contains("(O)") ? 0 :
                            rawResult.contains("(X)") ? 6 : -1;
                }
            }
        } else {
            if ("0".equals(mission.get(1))) {
                mission.set(1, "1");
                levelUp(home);
            }
            home.setMission(mission.toString());
            homeRepository.save(home);
            level = home.getLevel();
        }

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        List<String> words = allWords.stream()
                .limit(6)
                .map(Word::getWord)
                .toList();

        return new HomeResponse(
                day, level, mission, quizCount, quiz, a, words, result, content
        );
    }

    public List<WordResponse> showWords(String email) {

        Home home = homeRepository.findById(email)
                .orElseGet(() -> {
                    Home newHome = Home.builder()
                            .email(email)
                            .createDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                            .build();
                    return homeRepository.save(newHome);
                });

        if (home == null) {
            throw new IllegalStateException("Home 생성 실패 : email =" + email);
        }

        updateDailyStatus(home);

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        return allWords.stream()
                .limit(4)
                .map(word -> new WordResponse(
                        word.getNum(),
                        word.getWord(),
                        word.getDescription(),
                        word.getSubject()
                ))
                .toList();
    }

    public List<WordResponse> searchWords(SearchWordRequest request) {
        Home home = homeRepository.findById(request.getEmail()).orElse(null);
        updateDailyStatus(home);

        String search = request.getSearch();
        if (search == null || search.isEmpty()) {
            return new ArrayList<>();
        }

        if (home != null) {
            List<String> mission = new ArrayList<>();
            for (String s : home.getMission()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")) {
                mission.add(s.trim());
            }
            if ("0".equals(mission.get(2))) {
                mission.set(2, "1");
                levelUp(home);
            }
            home.setMission(mission.toString());
            homeRepository.save(home);
        }
        List<Word> words = wordRepository.findByWordContainingIgnoreCase(search);
        return words.stream()
                .map(word -> new WordResponse(word.getNum(), word.getWord(), word.getDescription(), word.getSubject()))
                .toList();
    }

    public WordResponse wordOne(WordOneRequest request) {
        Optional<Word> wordOpt = wordRepository.findById(request.getNum());
        if (wordOpt.isEmpty()) {
            return null;
        }
        Word word = wordOpt.get();

        Home home = homeRepository.findById(request.getEmail()).orElse(null);
        if (home != null) {
            List<String> mission = new ArrayList<>();
            for (String s : home.getMission()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")) {
                mission.add(s.trim());
            }
            if ("0".equals(mission.get(2))) {
                mission.set(2, "1");
                levelUp(home);
            }
            home.setMission(mission.toString());
            homeRepository.save(home);
        }

        return new WordResponse(
                word.getNum(),
                word.getWord(),
                word.getDescription(),
                word.getSubject()
        );
    }

    public int showNews(BasicRequest request) {
        Home home = homeRepository.findById(request.getEmail()).orElse(null);
        updateDailyStatus(home);
        List<String> mission = new ArrayList<>();
        if (home.getMission() == null || home.getMission().isEmpty()) {
            for (int i = 0; i < 4; i++) mission.add("0");
        } else {
            for (String s : home.getMission()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")) {
                mission.add(s.trim());
            }
        }
        if ("0".equals(mission.get(3))) {
            mission.set(3, "1");
            levelUp(home);
        }
        home.setMission(mission.toString());
        homeRepository.save(home);
        return 1;
    }

}