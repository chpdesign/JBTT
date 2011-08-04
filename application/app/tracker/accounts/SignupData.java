package tracker.accounts;

import tracker.Config;

import java.util.ArrayList;
import java.util.List;

public class SignupData {
	public String login;
	public String password;
	public String email;
	public String displayName;

	public List<String> validate() {
		int loginMinLength = Config.getInt("accounts.login.length.min");
		int loginMaxLength = Config.getInt("accounts.login.length.max");

		int passwordMinLength = Config.getInt("accounts.password.length.min");
		int passwordMaxLength = Config.getInt("accounts.password.length.max");


		List<String> errors = new ArrayList<String>();

		if (this.login.length() < loginMinLength) {
			errors.add("Минимальная длина логина - " + loginMinLength);
		}

		return errors;
	}
}
