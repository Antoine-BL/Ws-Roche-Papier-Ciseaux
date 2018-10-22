package cgg.informatique.abl.webSocket.dto.validation;

import cgg.informatique.abl.webSocket.dto.CompteDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
   @Override
   public void initialize(PasswordMatches constraintAnnotation) {
   }
   @Override
   public boolean isValid(Object obj, ConstraintValidatorContext context){
      CompteDto user = (CompteDto) obj;
      return user.getPassword().equals(user.getMatchingPassword());
   }
}
