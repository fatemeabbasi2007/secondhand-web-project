package org.example.secondhandweb.service;

import org.example.secondhandweb.Repository.AdvertisementRepository;
import org.example.secondhandweb.Repository.UserRepository;
import org.example.secondhandweb.dto.RegisterRequestDTO;
import org.example.secondhandweb.exception.*;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.model.User;
import static org.example.secondhandweb.exception.BadRequestException.*;
import static org.example.secondhandweb.exception.ForbiddenException.*;
import static org.example.secondhandweb.exception.NotFoundException.*;
import static org.example.secondhandweb.exception.ConflictException.*;
//import org.example.secondhandweb.repository.AdvertisementRepository;
//import org.example.secondhandweb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public UserService(UserRepository userRepository , AdvertisementRepository advertisementRepository ) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public boolean registerUser(RegisterRequestDTO request) {

        String pass = request.password();

        if (pass == null || pass.length() < 8) {
            throw new PassNotValidException(
                    "رمز عبور باید حداقل ۸ کاراکتر باشد."
            );
        }

        String fullName = request.fullName();

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "وارد کردن نام و نام خانوادگی اجباری است."
            );
        }

        String phoneNum = request.phoneNum();

        if (phoneNum == null ||
                phoneNum.length() != 11 ||
                !phoneNum.startsWith("09")) {

            throw new InvalidPhoneNumException(
                    "شماره تلفن وارد شده معتبر نیست."
            );
        }

        String email = request.email();

        String emailRegex =
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (email == null || !email.trim().matches(emailRegex)) {
            throw new IllegalArgumentException(
                    "فرمت ایمیل وارد شده معتبر نیست."
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(
                    "این نام کاربری قبلاً ثبت شده است."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "این ایمیل قبلاً ثبت شده است."
            );
        }

        if (userRepository.existsByPhoneNum(request.phoneNum())) {
            throw new PhoneNumAlreadyExistsException(
                    "این شماره تلفن قبلاً ثبت شده است."
            );
        }

        User newUser = new User();

        newUser.setId(UUID.randomUUID().toString());
        newUser.setUsername(request.username());
        newUser.setPassword(request.password());
        newUser.setEmail(request.email());
        newUser.setPhoneNum(request.phoneNum());
        newUser.setFullName(request.fullName());

        // Controlled by the server.
        newUser.setRole("USER");
        newUser.setEnabled(true);

        userRepository.save(newUser);

        return true;
    }


    public User loginUser(String username, String password) {

        User myUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "این شناسه کاربری وجود ندارد"
                        )
                );

        if (!myUser.getPassword().equals(password)) {
            throw new WrongPasswordException("پسوورد اشتباه");
        }

        if (!myUser.isEnabled()) {
            throw new UserBannedException("کاربر مسدود است");
        }

        return myUser;
    }

    public boolean addAdvertisementToFavorites(
            User currentUser,
            String adId) {

        if (!advertisementRepository.existsById(adId)) {
            throw new AdvertisementNotFoundException(
                    "آگهی مورد نظر وجود ندارد یا حذف شده است"
            );
        }

        List<String> favorites =
                currentUser.getFavoriteAdIds();

        if (favorites.contains(adId)) {
            throw new AdAlreadyFavoriteException(
                    "این آگهی قبلاً در لیست علاقه‌مندی‌های شما ذخیره شده است"
            );
        }

        favorites.add(adId);

        userRepository.save(currentUser);

        return true;
    }

    public boolean removeAdvertisementFromFavorites(
            User currentUser,
            String adId) {

        if (!advertisementRepository.existsById(adId)) {
            throw new AdvertisementNotFoundException(
                    "آگهی مورد نظر وجود ندارد یا حذف شده است"
            );
        }

        List<String> favorites =
                currentUser.getFavoriteAdIds();

        if (!favorites.contains(adId)) {
            throw new AdNotFavException(
                    "آگهی در لیست علاقه‌مندی نیست"
            );
        }

        favorites.remove(adId);

        userRepository.save(currentUser);

        return true;
    }

    public List<Advertisement> getUserFavoriteAdIds(
            User currentUser) {

        List<String> favoriteAdIds =
                currentUser.getFavoriteAdIds();

        if (favoriteAdIds == null || favoriteAdIds.isEmpty()) {
            return new ArrayList<>();
        }

        return advertisementRepository.findAllById(favoriteAdIds);
    }


    public List<User> getAllUsersForAdmin(User requester) {

        if (!"ADMIN".equalsIgnoreCase(requester.getRole())) {
            throw new NoAccessException(
                    "شما دسترسی لازم برای مشاهده این اطلاعات را ندارید!"
            );
        }

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        "USER".equalsIgnoreCase(user.getRole()))
                .toList();
    }


    public boolean blockUser(
            String userId,
            User admin) {

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new NoAccessException(
                    "برای مسدود کردن دسترسی ادمین لازم است"
            );
        }

        User user = userRepository.findByID(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "کاربر یافت نشد"
                        )
                );

        user.setEnabled(false);
        userRepository.save(user);

        return true;
    }

    public boolean unblockUser(
            String userId,
            User admin) {

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new NoAccessException(
                    "برای رفع مسدودیت دسترسی ادمین لازم است"
            );
        }

        User user = userRepository.findByID(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "کاربر یافت نشد"
                        )
                );

        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }
}