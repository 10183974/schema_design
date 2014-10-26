clear all;
close all;
clc;


xmin = [10,  4,     10,   10,    100,    4,    10,   10];
xmax = [10,  4,     10,   10,    100,    50,   50,   50];

Ns = 10;
filename = 'lhs_test.csv';
S=lhsu(xmin,xmax,Ns);
Sr = round(S);
disp(Sr);
fid = fopen(filename, 'w');

for row=1:Ns
     fprintf(fid, '%d,%d,%d,%d,%d,%d,%d,%d\n', Sr(row,:));
end
  

A = [30,60,90,120,150];
for i = 1:length(A)
  Ns = A(i);
  filename = ['lhs_train_',num2str(A(i)),'.csv'];
  S=lhsu(xmin,xmax,Ns);
  Sr = round(S);
  disp(Sr);

  fid = fopen(filename, 'w');

  for row=1:Ns
     fprintf(fid, '%d,%d,%d,%d,%d,%d,%d,%d\n', Sr(row,:));
  end
end


