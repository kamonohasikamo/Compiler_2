int a;
int f();
int cc();
int* bva();
void aa(),bb();

func int f() {
	int local;
	if (true) {
		return 3;
	} else {
		return 6;
	}
}

func void aa() {
	output a;
	return;
}

func int cc() {
	int b, c[20];
	a = 2;
	b = a + 3;
	c[19] = b + 5;
	output c[19];
	call aa();
	a = call f();
	//a = 5 + aa(); // voidŒ^‚È‚Ì‚Å‘«‚¹‚È‚¢ -> ƒGƒ‰[
	//aa();
	return 1 * 3 + a;
	//return a;
}

