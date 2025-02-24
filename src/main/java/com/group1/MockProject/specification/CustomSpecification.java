package com.group1.MockProject.specification;

import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import static java.lang.String.join;


public class CustomSpecification<T> {

    public static <T> Specification<T> sortByCriteria(String sortBy, String sortDir) {
        return (root, query, criteriaBuilder) -> {
            Join<Instructor, User> userJoin = root.join("user");
            if(sortDir.equalsIgnoreCase("asc")) {
                query.orderBy(criteriaBuilder.asc(userJoin.get(sortBy)));
            }
            else if(sortDir.equalsIgnoreCase("desc")) {
                query.orderBy(criteriaBuilder.desc(userJoin.get(sortBy)));
            }
            return null;
        };
    }
}
