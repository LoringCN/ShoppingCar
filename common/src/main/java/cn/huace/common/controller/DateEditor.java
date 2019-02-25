package cn.huace.common.controller;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huangdan on 2017/1/5.
 */
public class DateEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if(!StringUtils.isEmpty(text)){
            try {
                date = format.parse(text);
            } catch (ParseException e) {
                format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(text);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }
        setValue(date);
    }
}
