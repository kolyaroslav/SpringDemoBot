package ver1.SpringDemoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ver1.SpringDemoBot.сonfig.BotConfig;
import ver1.SpringDemoBot.model.User;
import ver1.SpringDemoBot.model.UserRepository;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT = "Это дипломная работа по созданию Бота,\nкоторый будет по номеру Контейнера или Коносамента выдавать инофрмацию\nо дате отплытия и дате прибытия в порт контейнера." +
            "\nНапишите /start для получения привественного сообщения.";
//кнопка /help
    public TelegramBot(BotConfig config) {
        this.config = config;
        //Кнопка меню Бота.
        List <BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get welcome message"));
//        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
//        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
        listOfCommands.add(new BotCommand("/info", "Информация о Боте"));
//        listOfCommands.add(new BotCommand("/settings","set your preferences"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Error settings command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
//Ендпоінти Бота
            switch (messageText) {
                case "/start":
                    reigsterUser(update.getMessage());

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/info":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:

                    sendMessage(chatId, "Я пока мало чего умею, но обязательно научусь!\n" + "Please Stand By");


            }

        }

    }

    private void reigsterUser(Message msg) {
        if(userRepository.findById(msg.getChatId()).isEmpty()){
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
        String answer = "Привет " + name + ", это Чат Бот Ярослава. \n" + "Это первая попытка создания бота.\n" + "Дальше будет интереснее.";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
