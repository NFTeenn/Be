    package nfteen.dondon.dondon.home.service;

    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import nfteen.dondon.dondon.home.dto.HomeRequest;
    import nfteen.dondon.dondon.home.dto.HomeResponse;
    import nfteen.dondon.dondon.home.entity.Home;
    import nfteen.dondon.dondon.home.repository.HomeRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    @Service
    public class HomeService {

        @Autowired
        private HomeRepository homeRepository;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String csvFilePath = "C:\\data\\don\\quiz.csv";

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
                    mission = objectMapper.readValue(home.getMission(), new TypeReference<List<String>>() {});
                    while (mission.size() < 4) mission.add("0");
                    mission.set(0, "1");
                    for (int i = 1; i < mission.size(); i++) {
                        if (!"1".equals(mission.get(i))) mission.set(i, "0");
                    }
                }
                home.setMission(objectMapper.writeValueAsString(mission));
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
                        .mission(objectMapper.writeValueAsString(mission))
                        .day(0)
                        .level(0)
                        .quizCount(0)
                        .build();
                homeRepository.save(newHome);

                day = 0;
                level = 0;
                quizCount = 0;
            }

            List<String[]> csvData = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] values = line.split(",");
                    csvData.add(values);
                }
            }

            Random rand = new Random();
            String quiz = "";
            List<String> a = null;
            String content = "";

            if (!csvData.isEmpty()) {
                String[] selected = csvData.get(rand.nextInt(csvData.size()));

                quiz = selected[2];
                content = selected[8];

                String type = selected[1];
                if ("객관식퀴즈".equals(type)) {
                    a = new ArrayList<>();
                    a.add(selected[3]);
                    a.add(selected[4]);
                    a.add(selected[5]);
                    a.add(selected[6]);
                } else if ("OX퀴즈".equals(type)) {
                    a = null;
                }
            }

            return new HomeResponse(day, level, mission, quizCount, quiz, a, content);
        }
    }
