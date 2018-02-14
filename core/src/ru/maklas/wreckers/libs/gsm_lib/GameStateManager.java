package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.graphics.Color;

/**
 * @author maklas. Created on 11.05.2017.
 */
public interface GameStateManager {

    /**
     * Обновляет все состояния которые требуют обновления.
     * Затем рисует их
     * @param dt Время за которое протекает кадр
     */
    void update(float dt);

    /**
     * Указывает всем состояниям что приложение ушло на задний план и не видимо больше
     */
    void toBackground();

    /**
     * Указывает всем состояниям что приложение ушло на передний план и снова видимо
     */
    void toForeground();

    /**
     * @return Текущий State (State находящийся на самом верху Стака)
     */
    State getCurrentState();

    /**
     * делает sout всех State в стаке
     */
    void printStackTrace();

    /**
     * Избавляется от всех стейтов и завершает работу.
     */
    void dispose();

    /**
     * @param command Комманда которая исполнится сразу после текущего кадра
     */
    void setCommand(GSMCommand command);

    /**
     * @param number номер State (снизу вверх)
     * @return <b>null</b> если State не найден
     */
    State getState(int number);

    void print(Object msg);

    void print(Object msg, float seconds);

    void print(Object msg, float seconds, Color color);

    void printAsync(Object msg, float seconds);
}
