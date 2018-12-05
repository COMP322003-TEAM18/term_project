package db_term_phase_3;

import java.util.ArrayList;

public class Menu {
	private ArrayList<String> menuStack;

	public Menu() {
		menuStack = new ArrayList<>();
	}

	public Menu(String menuName) {
		menuStack = new ArrayList<>();
		enter(menuName);
	}

	public void enter(String menuName) {
		menuStack.add(menuName);
	}

	public void leave() {
		menuStack.remove(menuStack.size() - 1);
	}

	public int depth() {
		return menuStack.size();
	}

	public String currentMenu() {
		return menuStack.get(menuStack.size() - 1);
	}

	public String path() {
		String temp = "";

		for (int i = 0; i < menuStack.size(); i++) {
			if (i != 0) {
				temp += "/";
			}
			temp += menuStack.get(i);
		}

		return temp;
	}

	@Override
	public String toString() {
		return path();
	}
}
