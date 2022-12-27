package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestReporistory requestReporistory;

    @Transactional
    @Override
    public ItemRequest createItemRequest(ItemRequestDto requestDto, int userId) {
        checkUserId(userId);
        ItemRequest temp = RequestMapper.toItemRequestDto(requestDto, userRepository.findUserByIdEquals(userId).get());
        temp.setCreated(LocalDateTime.now());
        return requestReporistory.save(temp);
    }

    @Override
    public List<ItemRequest> getRequests(int userId) {
        checkUserId(userId);
        List<ItemRequest> requestsList = requestReporistory.findByRequester_Id(userId);
        setItemListInRequestsList(requestsList);
        return requestsList;
    }

    @Override
    public List<ItemRequest> getRequestWithPagination(int userId, int from, int size) {
        checkUserId(userId);
        int start = from / size;
        List<ItemRequest> requestList = requestReporistory.findAllByRequester_IdNot(userId, PageRequest.of(start, size,
                Sort.by("created")))
                .stream()
                .collect(Collectors.toList());
        setItemListInRequestsList(requestList);
        return requestList;
    }

    @Override
    public ItemRequest getRequestListOnRequesterId(int requestId, int userId) {
        checkUserId(userId);
        if (!requestReporistory.existsById(requestId)) {
            throw new NotFoundException(String.format(
                    "Запрос с данным id %s не зарегистрирован.", requestId));
        }
        ItemRequest request = requestReporistory.findItemRequestByIdEquals(requestId);
        request.setItems(itemRepository.findAllByRequestIdEquals(requestId));
        return request;
    }

    public void checkUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(
                    "Пользователь с данным id %s не зарегистрирован.", userId));
        }
    }

    public void setItemListInRequestsList(List<ItemRequest> requestsList) {
        for (ItemRequest itemRequest : requestsList) {
            if (itemRepository.findAllByRequestIdEquals(itemRequest.getId()).size() > 0) {
                itemRequest.setItems(itemRepository.findAllByRequestIdEquals(itemRequest.getId()));
            }
            if (itemRequest.getItems() == null) {
                itemRequest.setItems(new ArrayList<>());
            }
        }
    }
}
