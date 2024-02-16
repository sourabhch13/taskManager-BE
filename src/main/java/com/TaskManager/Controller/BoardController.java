package com.TaskManager.Controller;

import com.TaskManager.Model.Board;
import com.TaskManager.Model.User;
import com.TaskManager.Services.BoardService;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.LongList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/board")
public class BoardController {

    @Autowired
    BoardService boardService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<Board>>> allBoard(){
        return new ResponseEntity<>(boardService.allBoard(), HttpStatusCode.valueOf(200));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Board>> deleteBoard(@PathVariable Long boardId, @RequestHeader("Authorization") String header){
        ApiResponse<Board> res = boardService.deleteBoard(boardId,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping(path = "/boardUser/{boardId}")
    public ResponseEntity<ApiResponse<List<User>>> thisBoardUser(@PathVariable Long boardId, @RequestHeader("Authorization") String header){
        ApiResponse<List<User>> res = boardService.thisBoardUser(boardId,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping(path = "myBoard")
    public ResponseEntity<ApiResponse<List<Board>>> getBoardForMe(@RequestHeader("Authorization") String header){
        ApiResponse<List<Board>> res = boardService.getBoardForMe(header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Board>> createBoard(@RequestBody Board board, @RequestHeader("Authorization") String header){
        ApiResponse<Board> res = boardService.createBoard(board, header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Board>> updateBoard(@RequestBody Board board, @RequestHeader("Authorization") String header){
        ApiResponse<Board> res = boardService.updateBoard(board,header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @PostMapping(path = "assignBoard/{boardId}")
    public ResponseEntity<ApiResponse<Board>> assignBoard(@PathVariable Long boardId, @RequestBody LongList idList, @RequestHeader("Authorization") String header){
        ApiResponse<Board> res = boardService.assignBoard(boardId, idList.getIdList(),header.substring(7));
        return new ResponseEntity<>(res,HttpStatusCode.valueOf(res.getStatusCode()));
    }
}
