const int a = 5;
int b;

{
	int c[10];
	const int b = 3;
	//int a;
	//b = 3;
	c[1] = a + b;
	//a[2] = 1;
}

{
	int b;
	//int b;
	//b = a;
	//b = c[0];
}