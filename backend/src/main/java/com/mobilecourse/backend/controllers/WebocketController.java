package com.mobilecourse.backend.controllers;

import com.mobilecourse.backend.WebSocketServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ws")
public class WebocketController extends CommonController {

    @RequestMapping(value = "/sendAll")
    String sendAllMsg(@RequestParam(value = "message") String msg,
                      @RequestParam(value = "roomid")int roomid) {
        WebSocketServer.broadcast(msg, roomid);
        return wrapperMsg(1, "OK");
    }
}
