package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;
import java.util.LinkedList;

public class EStrategy implements RecurrenceStrategy{

    private int n;
    private int point;

    private RecurrenceStrategy recurrenceStrategy;

    public EStrategy(String recurrence){
        this.point = recurrence.indexOf(".");
        this.n = Integer.parseInt(recurrence.substring(1, point));
        switch (recurrence.charAt(point+1)){
            case 'H':
                recurrenceStrategy = new HStrategy(recurrence.substring(point+1));
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(recurrence.substring(point+1));
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(recurrence.substring(point+1));
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(recurrence.substring(point+1));
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(recurrence.substring(point+1));
                break;
        }
    }

    public boolean[] getRecurrenceDays(){
        boolean []recurrenceDays = new boolean[7];
        int dias = n;
        for(int i = 0; i < 7; i++){
            recurrenceDays[i] = ((dias & 1) == 1);
            dias = dias >> 1;
        }
        return  recurrenceDays;
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        int day = currentDate.getDayOfWeek().getValue();
        boolean []recurrenceDays = getRecurrenceDays();

        OffsetDateTime nextDate = null;

        boolean find = false;
        for(int i = day; i<7; i++){
            if(recurrenceDays[i]){
                find = true;
                nextDate = currentDate.plusDays(i+1-day);
                break;
            }
        }

        if(!find){
            for(int i = 0; i<day; i++){
                if(recurrenceDays[i]){
                    nextDate = currentDate.minusDays(day-(i+1));
                    nextDate = recurrenceStrategy.add(nextDate);
                    break;
                }
            }
        }

        return nextDate;
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        int day = currentDate.getDayOfWeek().getValue(); //4
        boolean []recurrenceDays = getRecurrenceDays();

        OffsetDateTime nextDate = null;

        boolean find = false;
        for(int i = day-2; i>=0; i--){ //1
            if(recurrenceDays[i]){
                find = true;
                nextDate = currentDate.minusDays(day-(i+1));
                break;
            }
        }

        if(!find){
            for(int i = 6; i>=day; i++){
                if(recurrenceDays[i]){
                    nextDate = currentDate.plusDays(i+1-day);
                    nextDate = recurrenceStrategy.minus(nextDate);
                    break;
                }
            }
        }

        return  nextDate;
    }

    @Override
    public String getRecurrenceMessage() {
        StringBuilder message = new StringBuilder();
        int codDias = n;
        LinkedList<String> dias = new LinkedList<>();
        for(int i = 1; i<=7; i++) {
            if((codDias & 1) == 1){
                switch (i){
                    case 1 : dias.add("Lunes"); break;
                    case 2 : dias.add("Martes"); break;
                    case 3 : dias.add("Miercoles"); break;
                    case 4 : dias.add("Jueves"); break;
                    case 5 : dias.add("Viernes"); break;
                    case 6 : dias.add("Sabados"); break;
                    default : dias.add("Domingos"); break;
                }
            }
            codDias = codDias >> 1;
        }
        message.append("Todos los ");
        for(String dia : dias){
            if(dia.equals(dias.getLast())&&dias.size()>1){
                message.setLength(message.length()-2);
                message.append(" y ");
            }
            message.append(dia);
            message.append(", ");
        }
        message.append(recurrenceStrategy.getRecurrenceMessage().toLowerCase());
        return message.toString();
    }
}
