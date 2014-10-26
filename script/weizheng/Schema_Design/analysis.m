clear all;
close all;
clc;

 m = load('zscan_90_trainData.txt');
%  m = load('scan_testData.txt');

disp(m);
[~,nCol] = size(m);

figure(4);
col = ['r','b','g','c','k','y'];
mar = ['o','+','s','d','.','*'];

% leng = leng(1:nCol);
for i = 4:nCol
    j=i-3;
    [Y,I]=sort(m(:,i));
    A=m(I,:); 
    plot(A(:,i)/max(A(:,i)),A(:,1),'color',col(j),'marker',mar(j));
    
    hold on
end
title('Scan Query Latency','FontSize',18);
xlabel('Normalized Parameters','FontSize',14);
ylabel('Latency (ms)','FontSize',14);

leng = {'number of rows','result size'};
legend(leng,'Location','NorthWest','FontSize',14);
set(gca,'FontSize',14);
hold off;
saveas(gca, 'scan.eps','epsc');

