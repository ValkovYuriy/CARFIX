package yuriy.dev.carfixbackend.util;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yuriy.dev.carfixbackend.repository.MarkRepository;
import yuriy.dev.carfixbackend.repository.ModelRepository;

@Component
@RequiredArgsConstructor
public class Util {


    private final MarkRepository markRepository;


    private final ModelRepository modelRepository;


//    @PostConstruct
//    public void addDataWithHibernate(){
//        String filePath = "C:\\Users\\User\\University\\CARFIX\\Carfix-Backend\\src\\main\\resources\\data\\marks_models_years.csv";
//        Map<String, Mark> markMap = new HashMap<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split(";");
//
//                String markName = data[0].trim();
//                String modelName = data[1].trim();
//
//                Mark mark = markMap.computeIfAbsent(markName, name -> {
//                    Mark newMark = Mark.builder().markName(name).build();
//                    markRepository.save(newMark);
//                    return newMark;
//                });
//                Model model = Model.builder()
//                        .modelName(modelName)
//                        .mark(mark)
//                        .build();
//                modelRepository.save(model);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
