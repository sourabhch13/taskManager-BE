package com.TaskManager.Services;

import com.TaskManager.Model.Board;
import com.TaskManager.Model.BoardAssignment;
import com.TaskManager.Model.User;
import com.TaskManager.Repository.BoardAssignmentRepository;
import com.TaskManager.Repository.BoardRepository;
import com.TaskManager.Repository.BoardTaskTableRepository;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.LongList;
import com.TaskManager.UtilityClasses.ROLE;
import com.TaskManager.UtilityClasses.UserByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    private BoardAssignmentRepository boardAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardTaskTableRepository boardTaskTableRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserByToken userByToken;


    public ApiResponse<List<Board>> allBoard(){
        return new ApiResponse<>(200,"Success","List of all the boards",boardRepository.findAll());
    }

    public ApiResponse<Board> createBoard(Board board, String token){
        User u = userByToken.userByToken(token);
        Long userId = u.getId();
        if(u.getRole()!= ROLE.MANAGER) return new ApiResponse<>(400,"Failed","You cannot create board");
        if(board.getTitle().isEmpty()) return new ApiResponse<>(400,"Failed","Title cannot be empty");
        if(board.getDescription().isEmpty()) return new ApiResponse<>(400,"Failed","Description must be provided");
        board.setCreatedAt(LocalDate.now());
        board.setOwnerId(userId);
        boardRepository.save(board);
        BoardAssignment ba = new BoardAssignment();
        ba.setUserId(userId);
        ba.setBoardId(board.getBoardId());
        boardAssignmentRepository.save(ba);
        return new ApiResponse<>(201,"Created","Board created successfully");
    }

    public ApiResponse<Board> deleteBoard(Long boardId, String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<Board> b = boardRepository.findById(boardId);
        if(b.isPresent()){
            if(b.get().getOwnerId()==userId){
                List<Long> list = boardTaskTableRepository.findTaskIdByBoardId(boardId);
                for(Long i:list){
                    taskService.deleteTask(i,token);
                }
                boardTaskTableRepository.deleteByBoardId(boardId);
                boardAssignmentRepository.deleteByBoardId(boardId);
                boardRepository.deleteById(boardId);
                return new ApiResponse<>(200,"Success","Board deleted successfully");
            }
            return new ApiResponse<>(400,"Failed","Only owner of the board can delete");
        }
        return new ApiResponse<>(400,"Failed","No Board found");
    }

    public ApiResponse<List<Board>> getBoardForMe(String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> u = userRepository.findById(userId);
        if(!u.isPresent()) return  new ApiResponse<>(400,"Failed","User not found");
        List<Long> boardList = boardAssignmentRepository.findBoardIdByUserId(userId);
        List<Board> list = new ArrayList<>();
        for(Long i : boardList){
            Optional<Board> b = boardRepository.findById(i);
            if(b.isPresent()) list.add(b.get());
        }
        return new ApiResponse<>(200,"Success","List of board for this user",list);
    }

    public ApiResponse<Board> updateBoard(Board board, String token) {
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        if(board.getOwnerId()!=userId) return new ApiResponse<>(400,"Failed","Only owner of the board can make changes");
        if(board.getTitle().isEmpty()) return new ApiResponse<>(400,"Failed","Title cannot be empty");
        if(board.getDescription().isEmpty()) return new ApiResponse<>(400,"Failed","Description cannot be empty");
        Optional<Board> b = boardRepository.findById(board.getBoardId());
        if(b.isPresent()){
            Board B = b.get();
            B.setTitle(board.getTitle());
            B.setDescription(board.getDescription());
            boardRepository.save(B);
            return new ApiResponse<>(201,"Updated","Board is updated");
        }
        return new ApiResponse<>(400,"Failed","Invalid request");
    }

    public ApiResponse<Board> assignBoard(Long boardId, List<Long> idList, String token) {
        User u = userByToken.userByToken(token);
        Optional<Board> b = boardRepository.findById(boardId);
        if(!b.isPresent()) return new ApiResponse<>(400,"Failed","Board not found");
        if(u.getRole()!= ROLE.MANAGER) return new ApiResponse<>(400,"Failed","You cannot assign board");
        Long userId = u.getId();
        String list="";
        String list2="";
        for(Long i : idList){
            Optional<User> usr = userRepository.findById(i);
            if(usr.isPresent()){
                Long uI = usr.get().getId();
                if(boardAssignmentRepository.existsByBoardIdAndUserId(boardId,uI)){
                    list2+=(uI+" ");
                }
                else {
                    BoardAssignment ba = new BoardAssignment();
                    ba.setBoardId(boardId);
                    ba.setUserId(i);
                    boardAssignmentRepository.save(ba);
                }
            }
            else list+=(i+" ");
        }
        String message ="";
        if(list2.length()>0) message+=(list2+"Already assigned to this user, ");
        if(list.length()>0) message+=(list+"User not found, ");
        if(list2.length()>0 || list.length()>0) return new ApiResponse<>(201,"Created",message+"rest user assigned the board");
        return new ApiResponse<>(201,"Created","Board assigned");
    }

    public ApiResponse<List<User>> thisBoardUser(Long boardId, String token){
        Optional<Board> board = boardRepository.findById(boardId);
        if(!board.isPresent()) return new ApiResponse<>(400,"Failed","Board not found");
        Long userId = Long.parseLong(jwtHelper.getUserIdFromToken(token));
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) return new ApiResponse<>(400,"Failed","Invalid token");
        if(!boardAssignmentRepository.existsByBoardIdAndUserId(boardId,userId)) return new ApiResponse<>(400,"Failed","You cannot access this board");
        List<Long> list = boardAssignmentRepository.findUserIdByBoardId(boardId);
        List<User> listAns = new ArrayList<>();
        for(Long i : list){
            Optional<User> u =userRepository.findById(i);
            if(u.isPresent()) listAns.add(new User(u.get().getId(),u.get().getName(),u.get().getEmail(),u.get().getRole(),u.get().getDesignation()));
        }
        return new ApiResponse<>(200,"Success","List of all user of this board",listAns);
    }

}