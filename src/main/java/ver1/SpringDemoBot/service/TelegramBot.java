package ver1.SpringDemoBot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ver1.SpringDemoBot.model.*;
import ver1.SpringDemoBot.сonfig.BotConfig;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdsRepository adsRepository;
    final BotConfig config;
    static final String HELP_TEXT = "Це дипломана робота Ярослава Ковальчука по розробці Чат бота на Java.\n" +
            "За номером Контейнера, бот видає інформацію звідки та " + "\n" +
            "куди прямує контейнер," + " а також дату прибуття контейнера в порт" +
            "\n" +
            "Просто вставте нормер контейнера та відправте боту." + "\n" +
            "Якщо інформація є у відкритому доступі, Ви отримаєте відповідь.";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    //кнопка /help
    public TelegramBot(BotConfig config) {
        this.config = config;
        //Кнопка меню Бота.
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Привітальне сповіщення"));
        listOfCommands.add(new BotCommand("/info", "Інформація про Чат Бота"));
        listOfCommands.add(new BotCommand("/currency", "Курс валют НБУ"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error settings command list: " + e.getMessage());
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
//Ендпоінти Бота
                switch (messageText) {
                    case "/start":
                        reigsterUser(update.getMessage());

                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/info":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;

                    case "/currency":
                        prepareAndSendMessage(chatId, MinFin.getCurrency(messageText));
                        break;

                    default:

                        String response = Maersk.getContInfo(messageText);
                        if (StringUtils.isNotEmpty(response)){
                            prepareAndSendMessage(chatId, response);
                        } else if (StringUtils.isNotEmpty(ShipmentLink.getContInfoo(messageText))) {
                            prepareAndSendMessage(chatId, ShipmentLink.getContInfoo(messageText));
                        } else {
                            prepareAndSendMessage(chatId, "Інформація не знайдена.");
                        }






                }
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(YES_BUTTON)) {
                String text = "Вы нажали конопку Да. С ув. Ваш Кэп";
                executeEditMessageText(text, chatId, messageId);

            } else if (callBackData.equals(NO_BUTTON)) {
                String text = "Вы нажали конопку Нет. С ув. Ваш Кэп";
                executeEditMessageText(text, chatId, messageId);
            }

        }
    }

//    private void register(long chatId) { // Метод для кнопок след. действия под сообщением
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Нажмите кнопку Да или Нет");
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>(); // лист в листе с храниением кнопок
//        List<InlineKeyboardButton> rowInline = new ArrayList<>();
//        var yesButton = new InlineKeyboardButton();
//
//        yesButton.setText("Да");
//        yesButton.setCallbackData(YES_BUTTON);
//
//        var noButton = new InlineKeyboardButton();
//
//        noButton.setText("Нет");
//        noButton.setCallbackData(NO_BUTTON);
//
//        rowInline.add(yesButton);
//        rowInline.add(noButton);
//
//        rowsInline.add(rowInline);
//
//        markupInline.setKeyboard(rowsInline);
//        message.setReplyMarkup(markupInline);
//        executeMessage(message);
//    }

    private void reigsterUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setUserName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);


        }
    }

    private void startCommandReceived(long chatId, String name) {
        //создаем ответ с эмоджи
        String answer = EmojiParser.parseToUnicode("Привіт " + name + ", це Чат Бот Ярослава Ковальчука. \n"
                + "Цей бот створено для полегшення роботи Менеджерів ЗЕД та Логістів.\n"
                + "Просто вставте та відправте боту номер контейнера. \n"
                + "Якщо по контейнеру є інформація у відкритому доступі, Ви отримаєте відповідь." + ":blush:" + ":ship:\n"
                + "Для отримання курсу валют НБУ натисніть конпку /currency в головному меню.");
        // String answer = ;
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
//// добавляем кнопку под сообщением
//        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
////первый ряд кнопок
////        KeyboardRow row = new KeyboardRow();
////
////        row.remove(row);
////        row.remove(keyboardRows);
//////второй ряд кнопок
////        keyboardRows.add(row);
////        row = new KeyboardRow();
////        row.remove(keyboardRows);
////        row.remove("check my data");
////        row.remove("delete my data");
//
//        // список рядов
//        keyboardRows.removeAll(keyboardRows);
//
//
//        keyboardMarkup.getRemoveKeyboard();
//
//        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }

    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}") // метод для рекламних сповіщень
    private void sendAds() {
        var ads = adsRepository.findAll();
        var users = userRepository.findAll();
        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }

}
