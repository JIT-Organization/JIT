package com.justintime.jit.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class CommonServiceImplUtil {
    public void copySelectedProperties(Object request, Object dbData, HashSet<String> propertiesToBeChanged) {
        BeanWrapper srcWrapper = new BeanWrapperImpl(request);
        BeanWrapper targetWrapper = new BeanWrapperImpl(dbData);
        for (String property : propertiesToBeChanged) {
            if (srcWrapper.isReadableProperty(property)) {
                targetWrapper.setPropertyValue(property, srcWrapper.getPropertyValue(property));
            }
        }
    }
}
