package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import jssc.SerialPortException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;

public class Timing {
    String timeHolder = "T00:";
    String tx_state;
    String tx_id;
    ArrayList stateHolder;
    private int thresholdCount = 0;
    DateTime startTime, stopTime;

    SystemConfiguration systemConfiguration;

    final protected Timeline timeline;
    protected ObjectProperty<Integer> hrLeft, minLeft, secLeft, actualMinLeft, actualSecLeft;
    protected DecimalFormat decimalFormat = new DecimalFormat("00");
    protected String decimalSec, decimalMin, decimalHour;
    protected DateTime txTime;
    protected LocalTime active_threshold, first_threshold, second_threshold, final_threshold;
    protected ObjectProperty<Color> backgroundColor, messageColor;
    LayoutConfiguration config;
    

    public Timing(Timeline timeline) throws SQLException, ClassNotFoundException {
        System.out.println("In Timing: " + tx_state);
        config = (new LayoutConfiguration()).getConfig();
        this.timeline = timeline;

        secLeft = new SimpleObjectProperty<>(0);
        actualSecLeft = new SimpleObjectProperty<>();
        minLeft = new SimpleObjectProperty<>();
        actualMinLeft = new SimpleObjectProperty<>();
        hrLeft = new SimpleObjectProperty<>();
        backgroundColor = new SimpleObjectProperty<>();
        messageColor = new SimpleObjectProperty<>();

        active_threshold = DateTime.parse(timeHolder.concat(config.active_threshold)).toLocalTime();
//        System.out.println("Active threshold ====>"+ active_threshold);
        first_threshold = DateTime.parse(timeHolder.concat(config.first_threshold)).toLocalTime();
        second_threshold = DateTime.parse(timeHolder.concat(config.second_threshold)).toLocalTime();
//        System.out.println("Second threshold ==> "+ second_threshold);
        final_threshold = DateTime.parse(timeHolder.concat(config.final_threshold)).toLocalTime();

    }

    public DateTime getActive() {
        System.out.println("In getActive: " + tx_state);
        this.thresholdCount++;

        Color backgroundColour = getBackgroundColour();
        backgroundColor.set(backgroundColour);
        Color textColor = getMessageColour();
        messageColor.set(textColor);
        System.out.println("IN Start time ====>"+ txTime);
        startTime = txTime;
        stopTime = txTime.plusMillis(active_threshold.getMillisOfDay());
        return stopTime;
    }

    public DateTime getFirstTotal() {
        System.out.println("In getFirstTotal: " + tx_state);
        this.thresholdCount++;
        Color backgroundColour = getBackgroundColour();
        backgroundColor.set(backgroundColour);
        Color textColor = getMessageColour();
        messageColor.set(textColor);
        var activeMillis = getActive();
        return activeMillis.plusMillis(first_threshold.getMillisOfDay());
    }

    public DateTime getSecondTotal() {
        this.thresholdCount++;
        System.out.println("In getSecondTotal: " + tx_state);
        var firstMillis = getFirstTotal();
        return firstMillis.plusMillis(second_threshold.getMillisOfDay());
    }

    public DateTime getFinalTotal() {
        this.thresholdCount++;
        System.out.println("In getFinalTotal: " + tx_state);
        var secondMillis = getSecondTotal();
        return secondMillis.plusMillis(final_threshold.getMillisOfDay());
    }

    public long getCountDown (DateTime totalTime) {
        return totalTime.getMillis() - DateTime.now().getMillis();
    }

    public void countDown(long countDown, long previousThreshold) {
        System.out.println("In countDown: " + tx_state);
        var seconds = countDown /1000;
        var minutes = seconds/60;
        var hours = minutes/60;

        /*previous threshold values in hours, minutes and seconds*/
        var previousThresholdInSeconds = previousThreshold/1000;
//        System.out.println("PreviousThreshold =========> "+ previousThresholdInSeconds);
        var previousThresholdInMinutes = previousThresholdInSeconds/60;
        var previousThresholdInHours = previousThresholdInMinutes/60;

        decimalHour = decimalFormat.format(hours);
        hrLeft.set(org.joda.time.Duration.standardHours(Long.parseLong(decimalHour)).toStandardHours().getHours());

//        var min = org.joda.time.Duration.standardMinutes(minutes).toStandardMinutes().getMinutes();
        var min = org.joda.time.Duration.standardMinutes(0).toStandardMinutes().getMinutes();
        var minCount = min + ( (min/60) * 60 );
        decimalMin = decimalFormat.format(minCount);
        actualMinLeft.set(min);
        minLeft.set(org.joda.time.Duration.standardMinutes(Long.parseLong(decimalMin)).toStandardMinutes().getMinutes());

//        var sec = org.joda.time.Duration.standardSeconds(seconds).toStandardSeconds().getSeconds();
        var sec = org.joda.time.Duration.standardSeconds(0).toStandardSeconds().getSeconds();
        System.out.println("Sec: "+ sec);
        var secCount = sec + ( (sec/60) * 60 );
        System.out.println("SecCount: "+ secCount);

        decimalSec = decimalFormat.format(secCount);
        actualSecLeft.set(sec);
        secLeft.set(org.joda.time.Duration.standardSeconds(Long.parseLong(decimalSec)).toStandardSeconds().getSeconds());

        timeline.playFromStart();
    }

    public void updateCountDown () {
        System.out.println("In updateCountDown: " + actualSecLeft.getValue() + " " + secLeft.get());
        System.out.println("In updateCountDown => thresholdCount ====>: " + tx_state);
        String currentState = actualSecLeft.get() != null ? getState(actualSecLeft) : tx_state;
        this.tx_state = currentState;
        System.out.println("In updateCountDown => state ====>: " + this.tx_state);
//        runNextWarningCountdownByState();
            try {
                actualSecLeft.set(actualSecLeft.get() + 1);
                secLeft.set(secLeft.get() + 1);
                actualMinLeft.set(actualMinLeft.get() + 1);
            } catch (NullPointerException ex) {
//                System.out.println("Good ERR right here, hopefully");
            }
//        }
    }

    protected String getState(ObjectProperty<Integer> actualSeconds) {
        int value = actualSeconds.get();
        this.secLeft.set(actualSeconds.get());
        if (value <= active_threshold.getMillisOfDay()/1000){
            this.tx_state = "active";
        }else if (value > active_threshold.getMillisOfDay()/1000 && value <= (first_threshold.getMillisOfDay() + active_threshold.getMillisOfDay())/1000){
            this.tx_state = "first_notice";
        }else if (value > first_threshold.getMillisOfDay()/1000 && value <= (second_threshold.getMillisOfDay() + first_threshold.getMillisOfDay() + active_threshold.getMillisOfDay())/1000){
            this.tx_state = "second_notice";
        }else if (value > second_threshold.getMillisOfDay()/1000 && value <= (final_threshold.getMillisOfDay() + second_threshold.getMillisOfDay() + first_threshold.getMillisOfDay() + active_threshold.getMillisOfDay())/1000){
            this.tx_state = "final_notice";
        }else {
            this.tx_state = "no_answer";
        }

        try {
            updateTx(this.tx_state);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return this.tx_state;
    }

    protected String getSecondFromDuration(String tx_id, String tx_state) throws SQLException, ClassNotFoundException {
        this.tx_id = tx_id;
        tx_state = this.tx_state;
        var countdown = secLeft.get();
        countdown = countdown < -2 ? null : countdown;

        System.out.println("In getSecondFromDuration: " + actualSecLeft.getValue() + " " + minLeft.getValue() + " " + countdown);

        if (countdown != null && !Objects.equals(tx_state, "attended") && !Objects.equals(tx_state, "no_answer")) {
            if (actualSecLeft.getValue() != null) {
                backgroundColor.set(getBackgroundColour());
                if (actualSecLeft.getValue() >= 0) {
                    if (countdown == 59 && minLeft.getValue() >= 0) {
                        secLeft.set(org.joda.time.Duration.standardSeconds(0).toStandardSeconds().getSeconds());
                        minLeft.set(minLeft.get() + 1);
                    }
                } else {
                    countdown = 0;
                    if (!Objects.equals(this.tx_state, "attended")) runNextWarningCountdown(tx_id, tx_state);
                }
            }

            return ":" + decimalFormat.format(countdown);

        } else return ":" + decimalFormat.format(0);
    }

    protected String getMinuteFromDuration(String tx_id, String tx_state) {
        this.tx_id = tx_id;
        this.tx_state = tx_state;
//        System.out.println("In getMinuteFromDuration: " + tx_state);
        var countdown = minLeft.get();
        if (countdown != null && actualSecLeft != null) {
            if (actualSecLeft.getValue() >= 0) {
                if (countdown == 59 && hrLeft.getValue() > 0) {
                    minLeft.set(org.joda.time.Duration.standardMinutes(0).toStandardMinutes().getMinutes());
                    hrLeft.set(hrLeft.get() + 1);
                }
            } else {
                countdown = 0;
            }

            return ":" + decimalFormat.format(countdown);

        } else return ":" + decimalFormat.format(0);
    }

    protected String getHourFromDuration(String tx_id, String tx_state) {
        this.tx_id = tx_id;
        this.tx_state = tx_state;
        System.out.println("In getHourFromDuration: " + tx_state);

        var countdown = hrLeft.get();
        if (countdown != null) {
            if (countdown < 0) {
                countdown = 0;
            }

            return decimalFormat.format(countdown);
        } else return decimalFormat.format(0);
    }

    protected void runActive() {
        System.out.println("In runActive: " + tx_state);
        DateTime firstTotal = getActive();
        long countDown = getCountDown(firstTotal);
        countDown(countDown, 0);
    }

    protected void runFirstCountDown() throws SQLException, ClassNotFoundException {
        System.out.println("In runFirstCountDown: " + tx_state);
        updateTx("first_notice");
        DateTime firstTotal = getFirstTotal();
        long countDown = getCountDown(firstTotal);
        countDown(countDown, getActive().getMillis());
    }

    protected void runSecondCountDown() throws SQLException, ClassNotFoundException {
        System.out.println("In runSecondCountDown: " + tx_state);
        updateTx("second_notice");
        DateTime secondTotal = getSecondTotal();
        long countDown = getCountDown(secondTotal);
        countDown(countDown, getFirstTotal().getMillis());
    }

    protected void runFinalCountDown() throws SQLException, ClassNotFoundException {
        System.out.println("In runFinalCountDown: " + tx_state);
        updateTx("final_notice");
        DateTime finalTotal = getFinalTotal();
        long countDown = getCountDown(finalTotal);
        countDown(countDown, getSecondTotal().getMillis());
    }

    private void runNoAnswer() throws SQLException, ClassNotFoundException {
        System.out.println("In runNoAnswer: " + tx_state);
        updateTx("no_answer");
        Color backgroundColour = getBackgroundColour();
        backgroundColor.set(backgroundColour);
        Color textColor = getMessageColour();
        messageColor.set(textColor);
    }

    private void runAttended() throws SQLException, ClassNotFoundException {
        System.out.println("In runAttended: " + tx_state);
        Color backgroundColour = getBackgroundColour();
        backgroundColor.set(backgroundColour);
        Color textColor = getMessageColour();
        messageColor.set(textColor);
        updateTx("attended");
    }

    private void runInActive() throws SQLException, ClassNotFoundException {
        System.out.println("In runInActive: " + tx_state);
        Color backgroundColour = getBackgroundColour();
        backgroundColor.set(backgroundColour);
        Color textColor = getMessageColour();
        messageColor.set(textColor);
//        updateTx("in_active");
    }

    private void updateTx(String tx_state) throws SQLException, ClassNotFoundException {
        System.out.println("In updateTx: " + tx_state + " " + tx_id);
        this.tx_state = tx_state;
        Db connect = new Db("tx_display");
        HashMap<String, Object> tx = new HashMap<>();

        if (tx_id != null) {
            playTone(tx_state);

            int tx_no = Integer.parseInt(tx_id);
            tx.put("tx_state", tx_state);
//            System.out.println("Ogechi ===>"+ this.secLeft.
        ;
            connect.update(tx, "tx_no", tx_no);

            var result = connect.get("tx", " WHERE tx_no = '" + tx_no + "' ORDER BY id DESC LIMIT 1");
            result.next();

            int tx_id = result.getInt("id");
            if (!tx_state.equals(result.getString("tx_state"))) connect.update(tx, "id", tx_id, "tx");

        }
    }

    private void playRepeat(String tone, String repeat) throws SQLException, ClassNotFoundException {
        var repeater = Integer.parseInt(repeat);
        for (int i = 0; i <= repeater ; i++) {
            systemConfiguration.playAudio(tone);
            systemConfiguration.playStatus = false;
        }
    }

    private void playTone(String tx_state) throws SQLException, ClassNotFoundException {
        systemConfiguration = new SystemConfiguration();
        var sConfig = systemConfiguration.getConfig();

        switch (tx_state) {
            case "first_notice":
                playRepeat(sConfig.first_warning_tone, sConfig.first_warning_repeat);
                break;
            case "second_notice":
                playRepeat(sConfig.second_warning_tone, sConfig.second_warning_repeat);
                break;
            case "final_notice":
                playRepeat(sConfig.final_warning_tone, sConfig.final_warning_repeat);
                break;
        }
    }

    protected void runNextWarningCountdown(String tx_id, String tx_state) throws SQLException, ClassNotFoundException {
        this.tx_state = tx_state;
        this.tx_id = tx_id;
        System.out.println("In runNextWarningCountdown: " + tx_state);

        switch (thresholdCount) {
            case 0:
                runActive();
                break;
            case 1:
                runFirstCountDown();
                break;
            case 2:
                runSecondCountDown();
                break;
            case 3:
                runFinalCountDown();
                break;
            case 4:
                runNoAnswer();
                break;
            default:
                runInActive();
                break;
        }
    }

    protected void runNextWarningCountdownByState(String tx_state) throws SQLException, ClassNotFoundException {
        this.tx_state = tx_state;
        System.out.println("In runNextWarningCountdownByState: " + tx_state);

        switch (tx_state) {
            case "first_notice":
                runFirstCountDown();;
                break;
            case "second_notice":
                runSecondCountDown();
                break;
            case "final_notice":
                runFinalCountDown();
                break;
            case "no_answer":
                runNoAnswer();
                break;
            default:
                runActive();
                break;
        }
    }

    protected void startWarningCountdown(DateTime txTime, String tx_state, String tx_id) throws SQLException, ClassNotFoundException {
        System.out.println("In startWarningCountdown: " + tx_state);
        this.txTime = txTime;
        this.tx_state = tx_state == null ? "in_active" : tx_state;
        switch (Objects.requireNonNull(this.tx_state)) {
            case "active":
                runActive();
                break;
            case "first_notice":
                runFirstCountDown();
                break;
            case "second_notice":
                runSecondCountDown();
                break;
            case "final_notice":
                runFinalCountDown();
                break;
            case "no_answer":
                runNoAnswer();
                break;
            case "attended":
                runAttended();
                break;
            default:
                runInActive();
                break;
        }
    }

    public Paint background() {
        return backgroundColor.get();
    }

    public Paint message() {
        return messageColor.get();
    }

    private Color getBackgroundColour() {
        Color color;
        tx_state = tx_state == null ? "in_active" : tx_state;
//        System.out.println("in getBackgroundColor ++++++++++"+ this.tx_state);
        switch (tx_state) {
            case "active":
                color = config.active_background;
                break;
            case "first_notice":
                color = config.first_background;
                break;
            case "second_notice":
                color = config.second_background;
                break;
            case "final_notice":
                color = config.final_background;
                break;
            case "no_answer":
                color = config.no_answer_background;
                break;
            case "attended":
                color = config.attended_background;
                break;
            default:
                color = config.inactive_background;
                break;

        }

        return color;
    }

    private Color getMessageColour() {
        Color color;
        tx_state = tx_state == null ? "in_active" : tx_state;

        switch (tx_state) {
            case "active":
                color = config.active_message;
                break;
            case "first_notice":
                color = config.first_message;
                break;
            case "second_notice":
                color = config.second_message;
                break;
            case "final_notice":
                color = config.final_message;
                break;
            case "no_answer":
                color = config.no_answer_message;
                break;
            case "attended":
                color = config.attended_message;
                break;
            default:
                color = config.inactive_message;
                break;

        }

        return color;
    }

}
