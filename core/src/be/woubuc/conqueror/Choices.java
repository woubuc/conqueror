package be.woubuc.conqueror;

import be.woubuc.conqueror.focus.Movement;
import be.woubuc.conqueror.focus.Strategy;
import be.woubuc.conqueror.focus.Training;
import be.woubuc.conqueror.screens.ChoiceScreen;
import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Creates the choices UI, used in {@link ChoiceScreen}.
 *
 * Every choice category has 4 options, but only 3 are shown at a time. Players
 * cannot choose the same value they chose on the previous choice window of
 * the same category.
 */
public class Choices {
	
	private Game game;
	
	/**
	 * Initialises the choices manager
	 * @param game The main game instance
	 */
	public Choices(Game game) {
		this.game = game;
	}
	
	/**
	 * Creates the movement choice window
	 */
	void createMovementChoice() {
		System.out.println("Creating movement choices");
		List<Option<Movement>> options = new ArrayList<>();
		
		if (game.player.movement != Movement.EXPLORE) {
			options.add(new Option<>("Explore",
					Game.getDrawable("movement_explore.png"),
					Movement.EXPLORE,
					"Send out explorers to chart the unknown areas, so the army can march on more efficiently."
			));
		}
		
		if (game.player.movement != Movement.FORTIFY) {
			options.add(new Option<>("Fortify",
					Game.getDrawable("movement_fortify.png"),
					Movement.FORTIFY,
					"Make sure the frontline is protected before expanding our territory and conquering new lands."
			));
		}
		
		if (game.player.movement != Movement.RETREAT) {
			options.add(new Option<>("Retreat",
					Game.getDrawable("movement_retreat.png"),
					Movement.RETREAT,
					"Abandon the frontline and retreat until a new, stronger line of defense has been established. (not implemented yet)"
			));
		}
		
		if (game.player.movement != Movement.REGROUP) {
			options.add(new Option<>("Regroup",
					Game.getDrawable("movement_regroup.png"),
					Movement.REGROUP,
					"Attempt to bring the army together in strong groups, instead of spreading out. (not implemented yet)"
			));
		}
		
		createChoice("Give movement orders", options, (choice) -> {
			game.player.movement = choice.value;
			game.setScreen(game.gameScreen);
		});
	}
	
	/**
	 * Creates the strategy choice window
	 */
	void createStrategyChoice() {
		System.out.println("Creating strategy choices");
		List<Option<Strategy>> options = new ArrayList<>();
		
		if (game.player.strategy != Strategy.AVOID) {
			options.add(new Option<>("Avoid",
					Game.getDrawable("strategy_avoid.png"),
					Strategy.AVOID,
					"Avoid conflict with the enemy. Will attempt to leave some space between our frontlines and the enemy. (not implemented yet)"
			));
		}
		
		if (game.player.strategy != Strategy.CHARGE) {
			options.add(new Option<>("Charge",
					Game.getDrawable("strategy_charge.png"),
					Strategy.CHARGE,
					"Charge head-first into battle and try to overwhelm the enemy with surprise attacks and sheer power."
			));
		}
		
		if (game.player.strategy != Strategy.DEFEND) {
			options.add(new Option<>("Defend",
					Game.getDrawable("strategy_defend.png"),
					Strategy.DEFEND,
					"Stand ground and try to defend the current territory, instead of expanding."
			));
		}
		
		if (game.player.strategy != Strategy.PROVOKE) {
			options.add(new Option<>("Provoke",
					Game.getDrawable("strategy_provoke.png"),
					Strategy.PROVOKE,
					"Try to provoke the enemy to attack areas with more fighters, giving less defended areas a chance to recover. (not implemented yet)"
			));
		}
		
		createChoice("Choose battleground strategy", options, (choice) -> {
			game.player.strategy = choice.value;
			game.setScreen(game.gameScreen);
		});
	}
	
	/**
	 * Creates the training choice window
	 */
	void createTrainingChoice() {
		System.out.println("Creating training choices");
		List<Option<Training>> options = new ArrayList<>();
		
		if (game.player.training != Training.SWORDS) {
			options.add(new Option<>("Swordsman",
					Game.getDrawable("training_swords.png"),
					Training.SWORDS,
					"Standard military units. Efficient at slaying enemies, but far from invulnerable."
			));
		}
		
		if (game.player.training != Training.BOWS) {
			options.add(new Option<>("Bowman",
					Game.getDrawable("training_bows.png"),
					Training.BOWS,
					"Bowmen are very weak when defending, but in offense they can make it rain hell upon the enemy."
			));
		}
		
		if (game.player.training != Training.CANNONS) {
			options.add(new Option<>("Cannon",
					Game.getDrawable("training_cannons.png"),
					Training.CANNONS,
					"Training is very slow, but these cannons are nearly invincible on the battlefield."
			));
		}
		
		if (game.player.training != Training.MILITIA) {
			options.add(new Option<>("Militia",
					Game.getDrawable("training_militia.png"),
					Training.MILITIA,
					"Recruit anyone, as fast as possible. The army will quickly become low in skill but high in numbers."
			));
		}
		
		createChoice("Select recruitment policy", options, (choice) -> {
			game.player.training = choice.value;
			game.setScreen(game.gameScreen);
		});
	}
	
	/**
	 * Creates the actual dialog window, used by the other methods in this class.
	 * @param title The dialog title
	 * @param options The options
	 * @param onChosen Called when an option is chosen
	 */
	private <T> void createChoice(String title, List<Option<T>> options, Consumer<Option<T>> onChosen) {
		Table root = new Table();
		root.setFillParent(true);
		game.stage.addActor(root);
		
		Label.LabelStyle labelStyle = new Label.LabelStyle(game.assets.get("font-large.fnt"), Color.WHITE);
		Label.LabelStyle smallLabelStyle = new Label.LabelStyle(game.assets.get("font-small.fnt"), Color.WHITE);
		
		Table container = new Table();
		
		Label label = new Label(title, labelStyle);
		label.setAlignment(Align.center);
		container.add(label).colspan(3).pad(10);
		container.row();
		
		for (Option<T> option : options) {
			Table optionButton = new Table();
			optionButton.setTouchable(Touchable.enabled);
			
			Table image = new Table();
			image.add(new Image(option.drawable)).center();
			
			optionButton.add(image).height(34).pad(5).padTop(20);
			optionButton.row();
			optionButton.add(new Label(option.name, labelStyle)).pad(5).padBottom(10);
			optionButton.row();
			
			Label description = new Label(option.description, smallLabelStyle);
			description.setWrap(true);
			optionButton.add(description).width(150).height(30).pad(5);
			
			optionButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					container.getChildren().forEach(Actor::clearListeners);
					container.remove();
					game.stage.clear();
					onChosen.accept(option);
					return true;
				}
				
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					optionButton.setBackground(new TextureRegionDrawable(ColourUtils.getTexture(Globals.COLOUR_PANEL_ACTIVE)));
				}
				
				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					optionButton.setBackground((Drawable) null);
				}
			});
			
			container.add(optionButton).width(160).pad(5).padBottom(50);
		}
		root.add(container);
	}
	
	/**
	 * Data container that holds information about the possible options
	 */
	private class Option<T> {
		final Drawable drawable;
		final T value;
		
		final String name;
		final String description;
		
		Option(String name, Drawable drawable, T value, String description) {
			this.drawable = drawable;
			this.value = value;
			
			this.name = name;
			this.description = description;
		}
	}
}
