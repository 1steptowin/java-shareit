package ru.practicum.server;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareitPageRequest extends PageRequest {

    public ShareitPageRequest(int page, int size, Sort sort) {
        super(page > 0 ? page / size : 0, size, sort);
    }


    public ShareitPageRequest(int from, int size) {
        super(from > 0 ? from / size : 0, size, Sort.unsorted());
    }
}
