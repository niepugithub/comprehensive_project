package com.imooc.miaosha.validator;

import com.imooc.miaosha.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description:光有IsMobile注解是不行的，还需要校验器
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 10:15
 **/
public class IsMobileValidator implements ConstraintValidator<IsMobile,String>{

    private boolean required=false;
    @Override
    public void initialize(IsMobile isMobile) {
        required=isMobile.required();

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }else {
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
