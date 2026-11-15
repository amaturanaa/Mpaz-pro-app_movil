package com.example.pago_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.pago_service.controller.PagoControllerV2;
import com.example.pago_service.model.Pago;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<Pago, EntityModel<Pago>> {

    @Override
    public EntityModel<Pago> toModel(Pago pago) {
        return EntityModel.of(pago,
                linkTo(methodOn(PagoControllerV2.class).obtenerPago(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).listarPagos()).withRel("pagos"));  
            }
}