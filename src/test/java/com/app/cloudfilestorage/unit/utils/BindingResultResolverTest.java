package com.app.cloudfilestorage.unit.utils;

import com.app.cloudfilestorage.utils.BindingResultResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BindingResultResolverTest {

    @Mock
    private BindingResult bindingResult;

    @Test
    void getFirstMessage_withNonEmptyBindingResult_returnsFirstObjectMessage() {
        ObjectError firstObjectError = new ObjectError("first object", "first message");
        ObjectError secondObjectError = new ObjectError("second object", "second message");
        List<ObjectError> objectErrorList = List.of(firstObjectError, secondObjectError);

        when(bindingResult.getAllErrors())
                .thenReturn(objectErrorList);

        String firstMessage = BindingResultResolver.getFirstMessage(bindingResult);
        assertThat(firstMessage).isEqualTo(firstObjectError.getDefaultMessage());

        verify(bindingResult).getAllErrors();
    }

    @Test
    void getFirstMessage_withEmptyBindingResult_returnsDefaultMessage() {
        when(bindingResult.getAllErrors())
                .thenReturn(Collections.emptyList());

        String firstMessage = BindingResultResolver.getFirstMessage(bindingResult);
        assertThat(firstMessage).isNotBlank();

        verify(bindingResult).getAllErrors();
    }
}
