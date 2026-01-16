package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagopaAmountFormatTest {

    @Test
    public void marshalAmountHasTwoDecimalPlaces() throws JAXBException {
        CtFlussoRiversamento flow = new CtFlussoRiversamento();
        // set total amount to 1 with scale 2
        flow.setImportoTotalePagamenti(new BigDecimal("1.00"));

        JAXBContext context = JAXBContext.newInstance(CtFlussoRiversamento.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        JAXBElement<CtFlussoRiversamento> element = new JAXBElement<>(new QName("http://www.digitpa.gov.it/schemas/2011/Pagamenti/", "FlussoRiversamento"), CtFlussoRiversamento.class, flow);
        StringWriter writer = new StringWriter();
        marshaller.marshal(element, writer);
        String xml = writer.toString();

        // The marshalled xml should contain 1.00 (two decimals)
        assertTrue(xml.contains("1.00"), "Marshalled XML should contain amount formatted with two decimals");
    }
}
