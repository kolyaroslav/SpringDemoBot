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

static final String CONT_SIZE = "Об'єм морських контейнерів: \n\n"+
        "20 DV: загальний - 33,1 м3, \nкорисний - 25-29 м3 в залежності від вантажу.\n\n"+
        "40 DV: загальний - 67,5 м3, \nкорисний - 54-58 м3 в залежності від вантажу.\n\n"+
        "40 HC: загальний - 75,3 м3, \nкорисний - 64-68 м3 в залежності від вантажу.\n\n"+
        "45 HC: загальний - 86,1 м3, \nкорисний - до 77 м3 в залежності від вантажу.\n\n"
        ;

    static final String ERROR_TEXT = "Error occurred: ";

    //Кнопки меню Бота.
    public TelegramBot(BotConfig config) {
        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Привітальне сповіщення"));
        listOfCommands.add(new BotCommand("/info", "Інформація про Чат Бота"));
        listOfCommands.add(new BotCommand("/currency", "Курс валют НБУ"));
        listOfCommands.add(new BotCommand("/cont_size","Об'єм морських контейнерів"));

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

                    case "/cont_size":
                        prepareAndSendMessage(chatId, CONT_SIZE);
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

            }

        }




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
