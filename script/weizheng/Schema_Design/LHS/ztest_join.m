clear all;
close all;
clc;

% xmin = [1000,  4,      10,   10,    1000,   4,    10,   10];
% xmax = [1000,  100,    100,  100,   1E5,    100,  100,  100];

% xmin = [1000,  4,     10,   10,    1000,   4,    10,   10];
% xmax = [1000,  50,    50,   50,    1E4,    50,   50,   50];

% xmin = [10,  4,     10,   10,    1000,   4,    10,   10];
% xmax = [10,  4,     10,   10,    1E4,    50,   50,   50];


xmin = [1,  4,      10,   10,    1000,   4,    10,   10];
xmax = [1000,  100,    100,  100,    2E4,    100,  100,  100];

Ns = 10;
filename = 'ylhs_test.csv';
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
  filename = ['ylhs_train_',num2str(A(i)),'.csv'];
  S=lhsu(xmin,xmax,Ns);
  Sr = round(S);
  disp(Sr);

  fid = fopen(filename, 'w');

  for row=1:Ns
     fprintf(fid, '%d,%d,%d,%d,%d,%d,%d,%d\n', Sr(row,:));
  end


  

end

% 
% a = 1;
% b = 2;
% figure(1);
% scatter(S(:,a),S(:,b),200,'or');
% hold on;
% title('Latin Hypercube Sampling','FontSize',16);
% xlim([xmin(a),xmax(a)]);
% ylim([xmin(b),xmax(b)]);
% xlabel('variable #1 (rowS)','FontSize',14);
% ylabel('variable #2 (columnS)','FontSize',14);
% set(gca,'FontSize',14);
% grid on;
% hold off;
% 
% 
% 
