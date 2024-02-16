package com.TaskManager.Services;

import com.TaskManager.Model.Board;
import com.TaskManager.Model.User;
import com.TaskManager.Repository.BoardAssignmentRepository;
import com.TaskManager.Repository.BoardRepository;
import com.TaskManager.Repository.BoardTaskTableRepository;
import com.TaskManager.Repository.UserRepository;
import com.TaskManager.Security.JwtHelper;
import com.TaskManager.UtilityClasses.ApiResponse;
import com.TaskManager.UtilityClasses.DESIGNATION;
import com.TaskManager.UtilityClasses.ROLE;
import com.TaskManager.UtilityClasses.UserByToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private TaskService taskService;

    @Mock
    private BoardTaskTableRepository boardTaskTableRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserByToken userByToken;

    @Mock
    private BoardAssignmentRepository boardAssignmentRepository;

    @Test
    void allBoard() {
        List<Board> list = new ArrayList<>();
        list.add(new Board(1l,"title","description", LocalDate.now(),2l));
        list.add(new Board(2l,"title2","description2", LocalDate.now(),2l));
        Mockito.when(boardRepository.findAll()).thenReturn(list);
        ApiResponse<List<Board>> res = boardService.allBoard();
        assertEquals(list,res.getData());
    }

    @Test
    void createBoardWithUserRole() {
        Board board = new Board(1l,"title","description",LocalDate.now(),2l);
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.USER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<Board> res = boardService.createBoard(board,"xyz");
        assertEquals("You cannot create board",res.getMessage());
    }

    @Test
    void createBoardWithTitleEmpty() {
        Board board = new Board(1l,"","description",LocalDate.now(),2l);
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<Board> res = boardService.createBoard(board,"xyz");
        assertEquals("Title cannot be empty",res.getMessage());
    }

    @Test
    void createBoardWithEmptyDescription() {
        Board board = new Board(1l,"title","",LocalDate.now(),2l);
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<Board> res = boardService.createBoard(board,"xyz");
        assertEquals("Description must be provided",res.getMessage());
    }

    @Test
    void createBoard() {
        Board board = new Board(1l,"title","description",LocalDate.now(),2l);
        User user = new User(1l,"user","user@gmail.com","12345", ROLE.MANAGER, DESIGNATION.INTERN);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<Board> res = boardService.createBoard(board,"xyz");
        assertEquals("Board created successfully",res.getMessage());
    }

    @Test
    void deleteBoard() {
        Board board = new Board(1l,"title","description",LocalDate.now(),2l);
        Long boardId = 1l;
        String token = "xyz";
        List<Long> list = new ArrayList<>();
        list.add(3l);
        list.add(4l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("2");
        Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        Mockito.when(boardTaskTableRepository.findTaskIdByBoardId(boardId)).thenReturn(list);
        ApiResponse<Board> res = boardService.deleteBoard(boardId,token);
        assertEquals("Board deleted successfully",res.getMessage());
    }


    @Test
    void deleteBoardWithoutOwnerPriviledge() {
        Board board = new Board(1l,"title","description",LocalDate.now(),3l);
        Long boardId = 1l;
        String token = "xyz";
        List<Long> list = new ArrayList<>();
        list.add(3l);
        list.add(4l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("2");
        Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        ApiResponse<Board> res = boardService.deleteBoard(boardId,token);
        assertEquals("Only owner of the board can delete",res.getMessage());
    }

    @Test
    void deleteBoardEvenBoardDoesnotExist() {
        Long boardId = 1l;
        String token = "xyz";
        List<Long> list = new ArrayList<>();
        list.add(3l);
        list.add(4l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("2");
        Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
        ApiResponse<Board> res = boardService.deleteBoard(boardId,token);
        assertEquals("No Board found",res.getMessage());
    }


    @Test
    void getBoardForMe() {
        User user =new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(2l,"title2","description",LocalDate.now(),1l);
        Board board1 = new Board(3l,"title3","description",LocalDate.now(),1l);
        List<Long> list = new ArrayList<>();
        list.add(2l);
        list.add(3l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardAssignmentRepository.findBoardIdByUserId(1l)).thenReturn(list);
        Mockito.when(boardRepository.findById(2l)).thenReturn(Optional.of(board));
        Mockito.when(boardRepository.findById(3l)).thenReturn(Optional.of(board1));
        ApiResponse<List<Board>> res = boardService.getBoardForMe("xyz");
        assertEquals("List of board for this user",res.getMessage());
    }

    @Test
    void getBoardForMeWithoutValidUser() {
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<Board>> res = boardService.getBoardForMe("xyz");
        assertEquals("User not found",res.getMessage());
    }

    @Test
    void updateBoardNotByOwner() {
        Board board = new Board(1l,"title","description",LocalDate.now(),2l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        ApiResponse<Board> res = boardService.updateBoard(board,"xyz");
        assertEquals("Only owner of the board can make changes",res.getMessage());
    }

    @Test
    void updateBoardWithEmptyTitle() {
        Board board = new Board(1l,"","description",LocalDate.now(),1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        ApiResponse<Board> res = boardService.updateBoard(board,"xyz");
        assertEquals("Title cannot be empty",res.getMessage());
    }

    @Test
    void updateBoardWithEmptyDescription() {
        Board board = new Board(1l,"title","",LocalDate.now(),1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        ApiResponse<Board> res = boardService.updateBoard(board,"xyz");
        assertEquals("Description cannot be empty",res.getMessage());
    }

    @Test
    void updateBoardEvenBoardDoesnotExist() {
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<Board> res = boardService.updateBoard(board,"xyz");
        assertEquals("Invalid request",res.getMessage());
    }


    @Test
    void updateBoard() {
        Long boardId = 1l;
        String token = "xyz";
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Board> res = boardService.updateBoard(board,"xyz");
        assertEquals("Board is updated",res.getMessage());
    }

    @Test
    void assignBoardWhenBoardDoesnotExist() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        List<Long> idList = new ArrayList<>();
        idList.add(2l);
        idList.add(3l);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        ApiResponse<Board> res = boardService.assignBoard(1l,idList,"xyz");
        assertEquals("Board not found",res.getMessage());
    }

    @Test
    void assignBoardWithoutPriviledge() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        List<Long> idList = new ArrayList<>();
        idList.add(2l);
        idList.add(3l);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        ApiResponse<Board> res = boardService.assignBoard(1l,idList,"xyz");
        assertEquals("You cannot assign board",res.getMessage());
    }

    @Test
    void assignBoardWhenUserNotFoundAndAlreadyExist() {
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        User user2 = new User(2l,"user2","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user3 = new User(3l,"user3","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        List<Long> idList = new ArrayList<>();
        idList.add(2l);
        idList.add(3l);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findById(3l)).thenReturn(Optional.empty());
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,2l)).thenReturn(true);
        ApiResponse<Board> res = boardService.assignBoard(1l,idList,"xyz");
        assertEquals("2 Already assigned to this user, 3 User not found, rest user assigned the board",res.getMessage());
    }

    @Test
    void assignBoard() {
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.MANAGER,DESIGNATION.INTERN);
        User user2 = new User(2l,"user2","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user3 = new User(3l,"user3","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        List<Long> idList = new ArrayList<>();
        idList.add(2l);
        idList.add(3l);
        Mockito.when(userByToken.userByToken("xyz")).thenReturn(user);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findById(3l)).thenReturn(Optional.of(user3));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,2l)).thenReturn(false);
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,3l)).thenReturn(false);
        ApiResponse<Board> res = boardService.assignBoard(1l,idList,"xyz");
        assertEquals("Board assigned",res.getMessage());
    }

    @Test
    void thisBoardUserWhenNoBoard() {
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.empty());
        ApiResponse<List<User>> res = boardService.thisBoardUser(1l,"xyz");
        assertEquals("Board not found",res.getMessage());
    }

    @Test
    void thisBoardUserInvalidToken() {
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());;
        ApiResponse<List<User>> res = boardService.thisBoardUser(1l,"xyz");
        assertEquals("Invalid token",res.getMessage());
    }

    @Test
    void thisBoardUserIsNotAccessible() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,1l)).thenReturn(false);
        ApiResponse<List<User>> res = boardService.thisBoardUser(1l,"xyz");
        assertEquals("You cannot access this board",res.getMessage());
    }

    @Test
    void thisBoardUser() {
        User user = new User(1l,"user","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user2 = new User(2l,"user2","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        User user3 = new User(3l,"user3","user@gmail.com","12345",ROLE.USER,DESIGNATION.INTERN);
        Board board = new Board(1l,"title","description",LocalDate.now(),1l);
        List<Long> list = new ArrayList<>();
        list.add(2l);
        list.add(3l);
        Mockito.when(boardRepository.findById(1l)).thenReturn(Optional.of(board));
        Mockito.when(jwtHelper.getUserIdFromToken("xyz")).thenReturn("1");
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(boardAssignmentRepository.existsByBoardIdAndUserId(1l,1l)).thenReturn(true);
        Mockito.when(boardAssignmentRepository.findUserIdByBoardId(1l)).thenReturn(list);
        Mockito.when(userRepository.findById(2l)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findById(3l)).thenReturn(Optional.of(user3));
        ApiResponse<List<User>> res = boardService.thisBoardUser(1l,"xyz");
        assertEquals("List of all user of this board",res.getMessage());
    }
}