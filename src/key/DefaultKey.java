package key;

import config.Constants;
import util.Hashing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DefaultKey implements Key
{
	private static final String DEFAULT_PASSWORD = 
		"Sass mich wieder Junge , und ich werde Sie schneiden."
		+ "ساس لي مرة أخرى صبي و أنا سوف تخفض لك."
		+ "מציק לי שוב ילד ואני אחתוך לך."
		+ "Sass mig aftur strák og ég sker þig."
		+ "lancang saya lagi anak dan aku akan memotong Anda."
		+ "다시 소년 을 내게 말대꾸 나는 당신 을 잘라 것입니다."
		+ "Нахален мене повторно момче и јас ќе ви се намали."
		+ "Sass me de novo garoto e eu vou cortá-lo."
		+ "Sass меня снова мальчик , и я отрежу тебе."
		+ "Me Sass nuevo chico y yo voy a cortar."
		+ "Sass ฉันอีกครั้ง เด็ก และฉันจะ ตัดคุณ"
		+ "Sass mig igen pojke och jag ska skära dig."
		+ "Sass ma znovu chlapec a ja ťa rezať."
		+ "再び少年を私にSASSと私はあなたをカットします。";

	/* (non-Javadoc)
	 * @see key.Key#getKeyHash()
	 */
	@Override
	public byte[] getKeyHash()
	{
		return Hashing.hash(DEFAULT_PASSWORD.getBytes(Constants.CHARSET));
	}
}
