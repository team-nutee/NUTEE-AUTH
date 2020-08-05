package kr.nutee.auth.DTO.Resource;


import kr.nutee.auth.DTO.Response.Response;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ResponseResource extends EntityModel<Response> {

    public ResponseResource(Response response, Class c, Long id, Link... links){
        super(response,links);
        add(linkTo(c).slash(id).withSelfRel());
    }

    public ResponseResource(Response response, Class c, Link... links){
        super(response,links);
        add(linkTo(c).withSelfRel());
    }
}
