package co.secretonline.accessiblestep;

import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum StepMode implements StringRepresentable {
	OFF(0, "false", "options.off"),
	STEP(1, "step", "options.accessiblestep.step"),
	AUTO_JUMP(2, "autojump", "options.autoJump");

	public static final Codec<StepMode> CODEC;
	private final int id;
	private final String serializedId;
	private final String translationKey;

	private StepMode(int id, String serializedId, String translationKey) {
		this.id = id;
		this.serializedId = serializedId;
		this.translationKey = translationKey;
	}

	@NonNull
	public String getSerializedName() {
		return this.serializedId;
	}

	public int getId() {
		return this.id;
	}

	public String getKey() {
		return this.translationKey;
	}

	public static StepMode byId(int id) {
		for (StepMode mode : values()) {
			if (mode.id == id) {
				return mode;
			}
		}
		return OFF;
	}

	public static StepMode bySerialisedId(String id) {
		for (StepMode mode : values()) {
			if (mode.serializedId.equals(id)) {
				return mode;
			}
		}
		return OFF;
	}

	static {
		CODEC = StringRepresentable.fromEnum(StepMode::values);
	}
}
