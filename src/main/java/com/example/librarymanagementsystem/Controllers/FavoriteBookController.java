//package com.example.librarymanagementsystem.Controllers;
//
//import com.example.librarymanagementsystem.Entities.Book;
//import com.example.librarymanagementsystem.Entities.User;
//import com.example.librarymanagementsystem.Services.FavoriteBookService;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * Контроллер для управления избранными книгами пользователя.
// * Работает с ролью 'Студент' (USER).
// */
//@Controller
//@RequestMapping("/favorites")
//public class FavoriteBookController {
//
//    private final FavoriteBookService favoriteBookService;
//
//    public FavoriteBookController(FavoriteBookService favoriteBookService) {
//        this.favoriteBookService = favoriteBookService;
//    }
//
//    /**
//     * Отображает страницу со списком избранных книг текущего пользователя.
//     * @param model Модель для передачи данных в Thymeleaf.
//     * @param user Принципал Spring Security (текущий аутентифицированный пользователь).
//     * @return Название HTML-шаблона для отображения.
//     */
//    @GetMapping
//    public String viewFavorites(Model model, @AuthenticationPrincipal User user) {
//        if (user == null) {
//            // Если пользователь не аутентифицирован, перенаправляем на страницу входа
//            return "redirect:/login";
//        }
//        List<Book> favoriteBooks = favoriteBookService.getFavoriteBooksForUser(user.getId());
//        model.addAttribute("favoriteBooks", favoriteBooks);
//        model.addAttribute("pageTitle", "Мои избранные книги"); // Для заголовка страницы
//        return "user/favorites"; // Путь к вашему Thymeleaf шаблону, например: /resources/templates/user/favorites.html
//    }
//
//    /**
//     * Обрабатывает POST-запрос на добавление книги в избранное.
//     * @param bookId ID книги, которую нужно добавить.
//     * @param user Текущий пользователь.
//     * @param redirectAttributes Атрибуты для передачи сообщений после редиректа.
//     * @return Редирект на страницу книги.
//     */
//    @PostMapping("/add/{bookId}")
//    public String addToFavorites(@PathVariable("bookId") UUID bookId,
//                                 @AuthenticationPrincipal User user,
//                                 RedirectAttributes redirectAttributes) {
//        if (user == null) {
//            return "redirect:/login";
//        }
//        try {
//            favoriteBookService.addBookToFavorites(user.getId(), bookId);
//            redirectAttributes.addFlashAttribute("successMessage", "Книга успешно добавлена в избранное!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось добавить книгу в избранное. " + e.getMessage());
//        }
//        // Редирект обратно на страницу деталей книги
//        return "redirect:/books/" + bookId;
//    }
//
//    /**
//     * Обрабатывает POST-запрос на удаление книги из избранного.
//     * @param bookId ID книги, которую нужно удалить.
//     * @param user Текущий пользователь.
//     * @param redirectAttributes Атрибуты для передачи сообщений.
//     * @return Редирект на предыдущую страницу (список избранного или страница книги).
//     */
//    @PostMapping("/remove/{bookId}")
//    public String removeFromFavorites(@PathVariable("bookId") UUID bookId,
//                                      @AuthenticationPrincipal User user,
//                                      RedirectAttributes redirectAttributes) {
//        if (user == null) {
//            return "redirect:/login";
//        }
//        try {
//            favoriteBookService.removeBookFromFavorites(user.getId(), bookId);
//            redirectAttributes.addFlashAttribute("successMessage", "Книга удалена из избранного.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить книгу. " + e.getMessage());
//        }
//        // Можно сделать редирект на страницу избранного или обратно на страницу книги.
//        // Для этого можно передавать в запросе параметр с URL для возврата.
//        return "redirect:/favorites";
//    }
//}
