clear all;
close all;
clc;



xmin = [1000,   4,    10, 10];
xmax = [1E4,    100,  10, 100];

Ns = 300;
filename = 'zlhs_train.csv';
S=lhsu(xmin,xmax,Ns);
Sr_train = round(S);
disp(Sr_train);

fid = fopen(filename, 'w');

for row=1:Ns
    fprintf(fid, '%d,%d,%d,%d\n', Sr_train(row,:));
end


Ns = 10;
filename = 'zlhs_test.csv';
S=lhsu(xmin,xmax,Ns);
Sr_test = round(S);
disp(Sr_test);

fid = fopen(filename, 'w');

for row=1:Ns
    fprintf(fid, '%d,%d,%d,%d\n', Sr_test(row,:));
end



a = 1;
b = 2;
figure(1);
scatter(Sr_train(:,a),Sr_train(:,b),200,'or');
hold on;
title('Latin Hypercube Sampling','FontSize',16);
xlim([xmin(a),xmax(a)]);
ylim([xmin(b),xmax(b)]);
xlabel('variable #1 (rowS)','FontSize',14);
ylabel('variable #2 (columnS)','FontSize',14);
set(gca,'FontSize',14);
grid on;
hold off;



