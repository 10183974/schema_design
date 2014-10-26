clear all;
close all;
clc;

xmin = [0,    0];
xmax = [1E4,  100];

 
Ns = 10;
S=lhsu(xmin,xmax,Ns);
Sr = round(S);
disp(Sr);



a = 1;
b = 2;
figure(1);
scatter(S(:,a),S(:,b),300,'xb');
hold on;
title('Latin Hypercube Sampling','FontSize',18);
xlim([xmin(a),xmax(a)]);
ylim([xmin(b),xmax(b)]);
xlabel('variable #1','FontSize',14);
ylabel('variable #2','FontSize',14);
set(gca,'FontSize',14);
grid on;
hold off;
% 
% 
