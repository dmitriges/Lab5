/*
package ru.itmo.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
// определять какие поля сериализовать
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class DataMapper {
    private static final XmlMapper XML_MAPPER = createXmlMapper();
    // XmlMapper позволяет:
    // Превращать Java-объект в XML-строку
    //Превращать XML-строку обратно в Java-объект

    private DataMapper() {
    }

//применяем настройки к нашему экземпляру XmlMapper
    private static XmlMapper createXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        // Регистрируем модуль, без которого Jackson не сможет прочитать поля типа Instant
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // отключаем неудобную запись чисел в виде массива
        //После отключения этой настройки дата будет записана в ISO-строковом формате:
        //<createdAt>2026-04-11T15:30:00</createdAt>
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // исключает поля с null из xml
        return mapper;
    }

//
    public static XmlMapper getXml() {

        return XML_MAPPER;
    }
}
*/
