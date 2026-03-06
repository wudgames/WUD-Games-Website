package edu.wisc.wud.games.wud_games_website.rental_request;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the checkoutRecord value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = RentalRequestCheckoutRecordUnique.RentalRequestCheckoutRecordUniqueValidator.class
)
public @interface RentalRequestCheckoutRecordUnique {

    String message() default "{Exists.rentalRequest.checkout-record}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class RentalRequestCheckoutRecordUniqueValidator implements ConstraintValidator<RentalRequestCheckoutRecordUnique, Long> {

        private final RentalRequestService rentalRequestService;
        private final HttpServletRequest request;

        public RentalRequestCheckoutRecordUniqueValidator(
                final RentalRequestService rentalRequestService, final HttpServletRequest request) {
            this.rentalRequestService = rentalRequestService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(rentalRequestService.get(Long.parseLong(currentId)).getCheckoutRecord())) {
                // value hasn't changed
                return true;
            }
            return !rentalRequestService.checkoutRecordExists(value);
        }

    }

}

