package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserServiceImpl userService;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    @Override
    public Item createItem(Item item, int userId) {
        item.setOwner(userId);
        checkItem(item);
        return repository.save(item);
    }

    @Override
    public Item updateItem(Item item, int itemId, Integer userId) {
        if (userId == null) {
            throw new EmptyHeaderUserId("Отсутствует заголовок 'X-Sharer-User-Id'");
        } else if (!Objects.equals(repository.findItemByIdEquals(itemId).getOwner(), userId)) {
            throw new InvalidHeaderUserId
                    ("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        Item itemInStorage = getItem(itemId, userId);
        Item itemUpdate = ItemMapper.toDtoItem(ItemMapper.toItemDto(item), itemInStorage);
        itemUpdate.setId(itemId);
        return repository.save(itemUpdate);
    }

    @Override
    public Item getItem(int itemId, int userId) {
        LocalDateTime real = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemIdEquals(itemId);
        List<Comment> comments = commentsRepository.findAllByItemIdEquals(itemId);
        Item send = repository.findItemByIdEquals(itemId);
        if (!checkItemId(itemId)) {
            throw new IdItemOrUserNotExistException(String.format(
                    "Вещь с данным id %s не зарегистрирована.", itemId));
        } else if (!bookings.isEmpty()) {
            LastBooking lastBooking = null;
            NextBooking nextBooking = null;
            for (Booking booking : bookings) {
                if (booking.getEnd().isBefore(real)) {
                    lastBooking = BookingMapper.toLastBooking(booking);
                    continue;
                }
                if (booking.getStart().isAfter(real)) {
                    nextBooking = BookingMapper.toNextBooking(booking);
                    continue;
                }
                if (lastBooking != null && nextBooking != null) {
                    break;
                }
            }
            if (comments.size() != 0) {
                for (Comment comment : comments) {
                    if (comment.getText() != null) {
                        send.getComments().add(ItemMapper.toDtoComment
                                (comment, userService.getUser(comment.getAuthorId()).getName(), ""));
                    }
                }
            }
            if (userId != send.getOwner()) {
                return send;
            }
            send.setLastBooking(lastBooking);
            send.setNextBooking(nextBooking);
            return send;
        }
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                if (comment.getText() != null) {
                    send.getComments().add(ItemMapper.toDtoComment
                            (comment, userService.getUser(comment.getAuthorId()).getName(), ""));
                }
            }
        }
        return send;
    }

    @Override
    public Collection<Item> findAllItem() {
        return repository.findAll();
    }

    @Override
    public Collection<Item> getAllItems(Integer userId) {
        LocalDateTime real = LocalDateTime.now();
        List<Item> items = repository.findAllByOwnerEquals(userId);
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItemIdEquals(item.getId());
            if (!bookings.isEmpty()) {
                LastBooking lastBooking = null;
                NextBooking nextBooking = null;
                for (Booking booking : bookings) {
                    if (booking.getEnd().isBefore(real)) {
                        lastBooking = BookingMapper.toLastBooking(booking);
                        continue;
                    }
                    if (booking.getStart().isAfter(real)) {
                        nextBooking = BookingMapper.toNextBooking(booking);
                        continue;
                    }
                    if (lastBooking != null && nextBooking != null) {
                        break;
                    }
                }
                if (userId.equals(item.getOwner())) {
                    item.setLastBooking(lastBooking);
                    item.setNextBooking(nextBooking);
                }
            }
        }
        return items.stream()
                .sorted(new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return Integer.compare(o1.getId(), o2.getId());
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsOnDescription(String text) {
        if (text.isEmpty()) {
            List<Item> empty = new ArrayList<>();
            return empty;
        }
        return repository.findAllByDescriptionContainingIgnoreCase(text)
                .stream()
                .filter(x -> x.getAvailable() == true)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(int itemId) {
        repository.deleteItemById(itemId);
    }

    @Override
    public CommentDto createComment(Comment comment, int userId, int itemId) {
        if (comment.getText().isEmpty()) {
            throw new EmptyCommentTextException("Пустой комментарий");
        }
        boolean flag = true;
        List<Booking> test = bookingRepository.findAllByItemIdEqualsAndBookerIdEquals(itemId, userId);
        for (Booking booking : test) {
            if (booking.getStatus().equals(Status.APPROVED) && booking.getStart().isBefore(LocalDateTime.now())
                    && booking.getEnd().isBefore(LocalDateTime.now())) {
                flag = false;
            }
        }
        if (flag) {
            throw new NotBookingForUserException(String.format
                    ("Отсутствуют бронирования у пользователя с идентификатором %s", userId));
        }
        comment.setAuthorId(userId);
        comment.setItemId(itemId);
        return ItemMapper.toDtoComment(commentsRepository.save(comment), userService.getUser(userId).getName(), "");
    }

    public void checkItem(Item item) {
        if (Objects.equals(item.getName(), "") || item.getDescription() == null || item.getAvailable() == null || item.getOwner() == 0) {
            throw new EmptyFieldItemException("Отсутствует имя, описание, статус или владелец");
        } else if (userService.findAllUsers().stream().noneMatch(x -> x.getId() == item.getOwner())) {
            throw new NotFoundObjectException(String.format(
                    "Владельца с идентификатором %s не существует.", item.getOwner()));
        }
    }

    public boolean checkItemId(int itemId) {
        boolean isExistUser = false;
        for (Item value : repository.findAll()) {
            if (value.getId() == itemId) {
                isExistUser = true;
            }
        }
        return isExistUser;
    }
}
