package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    @Transactional
    @Override
    public Item createItem(Item item, int userId) {
        item.setOwner(userRepository.findUserByIdEquals(userId));
        checkItem(item);
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Item item, int itemId, Integer userId) {
        if (userId == null) {
            throw new EmptyHeaderUserId("Отсутствует заголовок 'X-Sharer-User-Id'");
        } else if (!Objects.equals(itemRepository.findItemByIdEquals(itemId).getOwner().getId(), userId)) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        Item itemInStorage = getItem(itemId, userId);
        Item itemUpdate = ItemMapper.toDtoItem(ItemMapper.toItemDto(item), itemInStorage);
        itemUpdate.setId(itemId);
        return itemRepository.save(itemUpdate);
    }

    @Override
    public Item getItem(int itemId, int userId) {
        LocalDateTime real = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAll().stream().filter(x -> x.getItem().getId() == itemId).collect(Collectors.toList());
        List<Comment> comments = commentsRepository.findAll().stream().filter(x -> x.getItem().getId() == itemId).collect(Collectors.toList());
        Item send = itemRepository.findItemByIdEquals(itemId);
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
                        send.getComments().add(ItemMapper.toDtoComment(comment, comment.getAuthor().getName(), ""));
                    }
                }
            }
            if (userId != send.getOwner().getId()) {
                return send;
            }
            send.setLastBooking(lastBooking);
            send.setNextBooking(nextBooking);
            return send;
        }
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                if (comment.getText() != null) {
                    send.getComments().add(ItemMapper.toDtoComment(comment, comment.getAuthor().getName(), ""));
                }
            }
        }
        return send;
    }

    @Override
    public Collection<Item> findAllItem() {
        return itemRepository.findAll();
    }

    @Override
    public Collection<Item> getAllItems(Integer userId) {
        LocalDateTime real = LocalDateTime.now();
        List<Item> items = itemRepository.findAll().stream().filter(x -> x.getOwner().getId() == userId).collect(Collectors.toList());
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAll().stream().filter(x -> x.getItem().getId() == item.getId()).collect(Collectors.toList());
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
                if (userId.equals(item.getOwner().getId())) {
                    item.setLastBooking(lastBooking);
                    item.setNextBooking(nextBooking);
                }
            }
        }
        return items.stream()
                .sorted(Comparator.comparingInt(Item::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsOnDescription(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByDescriptionContainingIgnoreCase(text)
                .stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteItem(int itemId) {
        itemRepository.deleteItemById(itemId);
    }

    @Transactional
    @Override
    public CommentDto createComment(Comment comment, int userId, int itemId) {
        if (comment.getText().isEmpty()) {
            throw new EmptyCommentTextException("Пустой комментарий");
        }
        boolean flag = true;
        List<Booking> test = bookingRepository.findAll().stream().filter(x -> (x.getItem().getId() == itemId) && (x.getUser().getId() == userId)).collect(Collectors.toList());
        for (Booking booking : test) {
            if (booking.getStatus().equals(Status.APPROVED) && booking.getStart().isBefore(LocalDateTime.now())
                    && booking.getEnd().isBefore(LocalDateTime.now())) {
                flag = false;
            }
        }
        if (flag) {
            throw new NotBookingForUserException(String.format("Отсутствуют бронирования у пользователя с идентификатором %s", userId));
        }
        comment.setAuthor(userRepository.findUserByIdEquals(userId));
        comment.setItem(itemRepository.findItemByIdEquals(itemId));
        return ItemMapper.toDtoComment(commentsRepository.save(comment), comment.getAuthor().getName(), "");
    }

    public void checkItem(Item item) {
        if (Objects.equals(item.getName(), "") || item.getDescription() == null || item.getAvailable() == null || item.getOwner().getId() == 0) {
            throw new EmptyFieldItemException("Отсутствует имя, описание, статус или владелец");
        } else if (userRepository.findAll().stream().noneMatch(x -> x.getId() == item.getOwner().getId())) {
            throw new NotFoundObjectException(String.format(
                    "Владельца с идентификатором %s не существует.", item.getOwner()));
        }
    }

    public boolean checkItemId(int itemId) {
        boolean isExistUser = false;
        for (Item value : itemRepository.findAll()) {
            if (value.getId() == itemId) {
                isExistUser = true;
                break;
            }
        }
        return isExistUser;
    }
}
