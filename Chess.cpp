#include <iostream>
#include <windows.h>

int main() {
	ShowWindow(GetConsoleWindow(), SW_HIDE);
	system("java Main");
}