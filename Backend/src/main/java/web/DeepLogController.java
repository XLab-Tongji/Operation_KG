package web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import service.LogAnalysis.java.src.deeplog;

import java.util.HashMap;
import java.util.List;

import static service.LogAnalysis.java.src.deeplog.*;

@RestController
public class DeepLogController {
    @RequestMapping(value = "/api/getPredictResult" , method = RequestMethod.GET ,produces = "application/json")
    public String getPredictResult() {
        try {
            return deeplog.predict();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
