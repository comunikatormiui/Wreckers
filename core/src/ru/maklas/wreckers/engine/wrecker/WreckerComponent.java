package ru.maklas.wreckers.engine.wrecker;

import ru.maklas.mengine.Component;

public class WreckerComponent implements Component {

	public float dullArmor; //Защита от прямых тяжелых ударов.  Рассчитывается по формуле лолки. 	Нормальные значения - 20..300
	public float sliceArmor;//Защита от ударов краем оружия.	Рассчитывается по формуле лолки. 	Нормальные значения - 20..300
	public float pierceArmor;//Защита от ударов углом оружия.   Рассчитывается по формуле лолки. 	Нормальные значения - 20..300
	public float stunResist;//Защита от стана.				  Рассчитывается по формуле лолки. 		Нормальные значения - 20..300
	public float disarmResist;//Защита от потери оружия при сильном ударе. Формула лолки.			Нормальные значения - 20..300
	public float stability; // Противодействие дополнительному отталкиванию. Формула лолки.	  		Нормальные значения - 20..300

	public WreckerComponent(float dullArmor, float sliceArmor, float pierceArmor, float stunResist, float disarmResist, float stability) {
		this.dullArmor = dullArmor;
		this.sliceArmor = sliceArmor;
		this.pierceArmor = pierceArmor;
		this.stunResist = stunResist;
		this.disarmResist = disarmResist;
		this.stability = stability;
	}

	public WreckerComponent() {

	}


	public WreckerComponent setAndRet(float dullArmor, float sliceArmor, float pierceArmor, float stunResist, float disarmResist, float stability) {
		this.dullArmor = dullArmor;
		this.sliceArmor = sliceArmor;
		this.pierceArmor = pierceArmor;
		this.stunResist = stunResist;
		this.disarmResist = disarmResist;
		this.stability = stability;
		return this;
	}

	public void set(WreckerComponent wc) {
		this.dullArmor = wc.dullArmor;
		this.sliceArmor = wc.sliceArmor;
		this.pierceArmor = wc.pierceArmor;
		this.stunResist = wc.stunResist;
		this.disarmResist = wc.disarmResist;
		this.stability = wc.stability;
	}

	public Object copy() {
		return new WreckerComponent(dullArmor, sliceArmor, pierceArmor, stunResist, disarmResist, stability);
	}

	@Override
	public String toString() {
		return "WreckerComponent{" +
				"dullArmor=" + dullArmor +
				", sliceArmor=" + sliceArmor +
				", pierceArmor=" + pierceArmor +
				", stunResist=" + stunResist +
				", disarmResist=" + disarmResist +
				", stability=" + stability +
				'}';
	}
}
