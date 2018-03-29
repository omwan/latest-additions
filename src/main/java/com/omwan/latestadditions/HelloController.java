package com.omwan.latestadditions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private DemoDocumentRepository demoDocumentRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    @ResponseBody
    public String test() {
        return "hello";
    }

    @RequestMapping(method = RequestMethod.POST, value ="/demodoc")
    @ResponseBody
    public DemoDocument addDoc(@RequestBody DemoDocument doc) {
        return demoDocumentRepository.save(doc);
    }

    @RequestMapping(method = RequestMethod.GET, value="/demodoc")
    @ResponseBody
    public List<DemoDocument> getAllDocs() {
        return demoDocumentRepository.findAll();
    }
}
