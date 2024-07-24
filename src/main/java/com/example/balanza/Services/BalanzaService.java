package com.example.balanza.Services;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BalanzaService {
    private SerialPort serialPort;
    private String lastWeight = "";
    private StringBuilder inputBuffer = new StringBuilder();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void connect(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Connected to port " + portName);

            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }

                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(newData, newData.length);
                    for (int i = 0; i < numRead; i++) {
                        char c = (char) newData[i];
                        System.out.print(c); // Imprimir cada carácter leído para depuración
                        if (c == '\n') { // Fin de línea
                            lastWeight = inputBuffer.toString().trim();
                            inputBuffer.setLength(0); // Limpiar buffer
                            System.out.println("Received weight: " + lastWeight);
                            messagingTemplate.convertAndSend("/topic/weight", lastWeight); // Broadcast weight to clients
                        } else {
                            inputBuffer.append(c); // Acumular bytes en el buffer
                        }
                    }
                }
            });
        } else {
            System.out.println("Could not open port " + portName);
        }
    }

    public void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Disconnected from port");
        }
    }

    public String getLastWeight() {
        return lastWeight;
    }
}
