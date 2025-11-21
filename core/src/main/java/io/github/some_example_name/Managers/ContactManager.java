package io.github.some_example_name.Managers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.objects.DoodleObject;
import io.github.some_example_name.objects.PlateObject;

public class ContactManager {

    World world;
    private DoodleObject doodle;

    public ContactManager(World world, DoodleObject doodle) {
        this.world = world;
        this.doodle = doodle;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object objA = contact.getFixtureA().getUserData();
                Object objB = contact.getFixtureB().getUserData();

                handleContact(objA, objB, true);
            }

            @Override
            public void endContact(Contact contact) {
                Object objA = contact.getFixtureA().getUserData();
                Object objB = contact.getFixtureB().getUserData();

                handleContact(objA, objB, false);
            }

            private void handleContact(Object objA, Object objB, boolean begin) {
                if (objA instanceof DoodleObject && objB instanceof PlateObject) {
                    handlePlatformContact((DoodleObject) objA, (PlateObject) objB, begin);
                } else if (objB instanceof DoodleObject && objA instanceof PlateObject) {
                    handlePlatformContact((DoodleObject) objB, (PlateObject) objA, begin);
                }
            }

            private void handlePlatformContact(DoodleObject doodle, PlateObject platform, boolean begin) {
                if (begin) {
                    // ПРОСТАЯ И НАДЕЖНАЯ ПРОВЕРКА
                    float doodleVelocityY = doodle.body.getLinearVelocity().y;

                    // ТОЛЬКО если дудл действительно падает (не прыгает вверх)
                    if (doodleVelocityY <= 2f) { // Очень строгая проверка
                        doodle.setOnPlatform(true);
                    }
                } else {
                    // ПРИ РАЗРЫВЕ КОНТАКТА ВСЕГДА СБРАСЫВАЕМ ПЛАТФОРМУ
                    doodle.setOnPlatform(false);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                Object objA = contact.getFixtureA().getUserData();
                Object objB = contact.getFixtureB().getUserData();

                if ((objA instanceof DoodleObject && objB instanceof PlateObject) ||
                    (objB instanceof DoodleObject && objA instanceof PlateObject)) {

                    DoodleObject doodle = objA instanceof DoodleObject ? (DoodleObject) objA : (DoodleObject) objB;
                    PlateObject platform = objA instanceof PlateObject ? (PlateObject) objA : (PlateObject) objB;

                    float doodleY = doodle.getY();
                    float platformY = platform.getY();
                    float doodleHeight = doodle.height;
                    float platformHeight = platform.height;

                    float doodleBottom = doodleY - doodleHeight / 2f;
                    float platformTop = platformY + platformHeight / 2f;
                    float doodleVelocityY = doodle.body.getLinearVelocity().y;

                    // СТРОГАЯ ПРОВЕРКА - дудл должен быть над платформой и падать
                    boolean isAbovePlatform = doodleBottom >= platformTop - 10f;
                    boolean isFalling = doodleVelocityY <= 1f;

                    boolean shouldCollide = isAbovePlatform && isFalling;

                    contact.setEnabled(shouldCollide);
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }
}
