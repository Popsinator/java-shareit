package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
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

    private LastBooking lastBooking;
    private NextBooking nextBooking;

    @Transactional
    @Override
    public Item createItem(Item item, int userId) {
        item.setOwner(userRepository.findUserByIdEquals(userId).get());
        checkItem(item);
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Item item, int itemId, Integer userId) {
        /*if (userId == null) {
            throw new InternalServerErrorException("Отсутствует заголовок 'X-Sharer-User-Id'");
        } else*/
        if (!Objects.equals(itemRepository.findItemByIdEquals(itemId).getOwner().getId(), userId)) {
            throw new NotFoundException("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        Item itemInStorage = getItem(itemId, userId);
        Item itemUpdate = ItemMapper.toDtoItem(ItemMapper.toItemDto(item), itemInStorage);
        itemUpdate.setId(itemId);
        return itemRepository.save(itemUpdate);
    }

    @Override
    public Item getItem(int itemId, int userId) {
        lastBooking = null;
        nextBooking = null;
        LocalDateTime real = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItem_Id(itemId);
        List<Comment> comments = commentsRepository.findAllByItem_Id(itemId);
        Item send = itemRepository.findItemByIdEquals(itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format(
                    "Вещь с данным id %s не зарегистрирована.", itemId));
        } else if (!bookings.isEmpty()) {
            setLastAndNextBooking(real, bookings);
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
    public List<Item> getAllItems(Integer userId) {
        lastBooking = null;
        nextBooking = null;
        LocalDateTime real = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
            if (!bookings.isEmpty()) {
                setLastAndNextBooking(real, bookings);
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
        /*if (comment.getText().isEmpty()) {
            throw new BadRequestException("Пустой комментарий");
        }*/
        boolean flag = true;
        List<Booking> test = bookingRepository.findAllByItem_IdAndAndBooker_Id(itemId, userId);
        for (Booking booking : test) {
            if (booking.getStatus().equals(Status.APPROVED) && booking.getStart().isBefore(LocalDateTime.now())
                    && booking.getEnd().isBefore(LocalDateTime.now())) {
                flag = false;
            }
        }
        if (flag) {
            throw new BadRequestException(String.format("Отсутствуют бронирования у пользователя с идентификатором %s", userId));
        }
        comment.setAuthor(userRepository.findUserByIdEquals(userId).get());
        comment.setItem(itemRepository.findItemByIdEquals(itemId));
        return ItemMapper.toDtoComment(commentsRepository.save(comment), comment.getAuthor().getName(), "");
    }

    public void checkItem(Item item) {
        if (//Objects.equals(item.getName(), "")
            //|| item.getDescription() == null
            //|| item.getAvailable() == null
            /*||*/ item.getOwner().getId() == 0) {
            throw new BadRequestException("Отсутствует владелец");
        } else if (!userRepository.existsById(item.getOwner().getId())) {
            throw new NotFoundException(String.format(
                    "Владельца с идентификатором %s не существует.", item.getOwner()));
        }
    }

    public void setLastAndNextBooking(LocalDateTime real, List<Booking> bookings) {
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
    }
}
