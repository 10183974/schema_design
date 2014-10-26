clear all;
clc;
close all;

figure;

y0 = load('zjoin_testRealLatency.txt');

x = 1:length(y0);
 plot(x,y0,'-ko');

hold on;


size = [30,60,90,120,150];
col = ['r','b','g','c','y'];
mar = ['+','s','d','.','*'];

for i = 1:4
    str = ['zjoin_',num2str(size(i)),'_testEstimateLatency.txt'];
    y = load(str);

     plot(x,y,'color',col(i),'marker',mar(i));
   
%     disp(abs(y-y0)./y0);
%       plot(x,abs(y-y0)./y0,'color',col(i),'marker',mar(i));

%     rrse = sqrt(sum((y-y0).^2)/sum((y0-mean(y0)).^2));
    rrse = sum(abs(y-y0))/sum(y0);
    disp([size(i),rrse]);
    
    hold on;
end
hold off;
grid minor;
xlabel('Test case','FontSize',14);
ylabel('latency (ms)','FontSize',14);
ylim()
legend('real','estimate with 30','estimate with 60','estimate with 90','estimate with 120','estimate with 150');
set(gca,'FontSize',16);

